package ch.rasc.eds.starter.service;

import static ch.ralscha.extdirectspring.annotation.ExtDirectMethodType.STORE_READ;
import static ch.rasc.eds.starter.entity.QAccessLog.accessLog;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;

import net.sf.uadetector.ReadableUserAgent;
import net.sf.uadetector.UserAgentStringParser;
import net.sf.uadetector.service.UADetectorServiceFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ch.ralscha.extdirectspring.annotation.ExtDirectMethod;
import ch.ralscha.extdirectspring.bean.ExtDirectStoreReadRequest;
import ch.ralscha.extdirectspring.bean.ExtDirectStoreResult;
import ch.ralscha.extdirectspring.filter.StringFilter;
import ch.rasc.eds.starter.entity.AccessLog;
import ch.rasc.eds.starter.repository.AccessLogRepository;
import ch.rasc.edsutil.QueryUtil;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.common.util.concurrent.AtomicLongMap;
import com.mysema.query.SearchResults;
import com.mysema.query.Tuple;
import com.mysema.query.jpa.JPQLQuery;
import com.mysema.query.jpa.impl.JPADeleteClause;
import com.mysema.query.jpa.impl.JPAQuery;

@Service
public class AccessLogService {

	private final static BigDecimal ONE_HUNDRED = new BigDecimal("100");

	private final Environment environment;

	private final EntityManager entityManager;

	private final AccessLogRepository accessLogRepository;

	@Autowired
	public AccessLogService(Environment environment, EntityManager entityManager,
			AccessLogRepository accessLogRepository) {
		this.environment = environment;
		this.entityManager = entityManager;
		this.accessLogRepository = accessLogRepository;
	}

	@ExtDirectMethod(STORE_READ)
	@PreAuthorize("hasRole('ADMIN')")
	@Transactional(readOnly = true)
	public ExtDirectStoreResult<AccessLog> read(ExtDirectStoreReadRequest request) {

		JPQLQuery query = new JPAQuery(entityManager).from(accessLog);

		if (!request.getFilters().isEmpty()) {
			StringFilter emailFilter = (StringFilter) request.getFilters().iterator()
					.next();
			String email = emailFilter.getValue();
			query.where(accessLog.email.containsIgnoreCase(email));
		}

		QueryUtil
				.addPagingAndSorting(query, request, AccessLog.class, accessLog,
						Collections.<String, String> emptyMap(),
						Collections.singleton("browser"));

		SearchResults<AccessLog> searchResult = query.listResults(accessLog);

		return new ExtDirectStoreResult<>(searchResult.getTotal(),
				searchResult.getResults());
	}

	@ExtDirectMethod(STORE_READ)
	@PreAuthorize("hasRole('ADMIN')")
	@Transactional(readOnly = true)
	public Collection<Map<String, Integer>> readAccessLogYears() {
		JPQLQuery query = new JPAQuery(entityManager).from(accessLog);
		List<Integer> years = query.distinct().list(accessLog.logIn.year());
		return years.stream().map(year -> Collections.singletonMap("year", year))
				.collect(Collectors.toList());
	}

	@ExtDirectMethod
	@PreAuthorize("hasRole('ADMIN')")
	@Transactional
	public void deleteAll() {
		new JPADeleteClause(entityManager, accessLog).execute();
	}

	@ExtDirectMethod(STORE_READ)
	@PreAuthorize("hasRole('ADMIN')")
	@Transactional(readOnly = true)
	public List<Map<String, Object>> readUserAgentsStats(int queryYear) {
		JPQLQuery query = new JPAQuery(entityManager).from(accessLog);
		query.where(accessLog.logIn.year().eq(queryYear));
		query.groupBy(accessLog.logIn.year(), accessLog.logIn.month(),
				accessLog.userAgentName);
		query.orderBy(accessLog.logIn.year().asc(), accessLog.logIn.month().asc());
		List<Tuple> queryResult = query.list(accessLog.logIn.year(),
				accessLog.logIn.month(), accessLog.userAgentName, accessLog.count());

		String[] browsers = new String[] { "Chrome", "Firefox", "IE", "Opera", "Safari" };

		AtomicLongMap<String> monthYearTotal = AtomicLongMap.create();
		Map<String, AtomicLongMap<String>> monthYearUACount = Maps.newTreeMap();

		for (Tuple tuple : queryResult) {
			Integer year = tuple.get(accessLog.logIn.year());
			Integer month = tuple.get(accessLog.logIn.month());
			Long count = tuple.get(accessLog.count());
			String userAgentName = tuple.get(accessLog.userAgentName);
			String key = year + "/" + (month < 10 ? "0" : "") + month;

			monthYearTotal.addAndGet(key, count);

			AtomicLongMap<String> uas = monthYearUACount.get(key);
			if (uas == null) {
				uas = AtomicLongMap.create();
				monthYearUACount.put(key, uas);
			}

			if (Arrays.binarySearch(browsers, userAgentName) >= 0) {
				uas.addAndGet(userAgentName, count);
			}
			else if (userAgentName.equals("Mobile Safari")) {
				uas.addAndGet("Safari", count);
			}
			else {
				uas.addAndGet("Other", count);
			}
		}

		List<Map<String, Object>> result = new ArrayList<>();

		for (String yearMonth : monthYearUACount.keySet()) {
			long total = monthYearTotal.get(yearMonth);
			ImmutableMap.Builder<String, Object> builder = ImmutableMap.builder();
			builder.put("yearMonth", yearMonth);

			for (Map.Entry<String, Long> entry : monthYearUACount.get(yearMonth).asMap()
					.entrySet()) {
				builder.put(
						entry.getKey(),
						new BigDecimal(entry.getValue()).multiply(ONE_HUNDRED).divide(
								new BigDecimal(total), 2, BigDecimal.ROUND_DOWN));
			}

			result.add(builder.build());
		}

		return result;
	}

	@ExtDirectMethod(STORE_READ)
	@PreAuthorize("hasRole('ADMIN')")
	@Transactional(readOnly = true)
	public List<Map<String, Object>> readOsStats(int queryYear) {
		JPQLQuery query = new JPAQuery(entityManager).from(accessLog);
		query.where(accessLog.logIn.year().eq(queryYear));
		query.groupBy(accessLog.operatingSystem);
		List<Tuple> queryResult = query
				.list(accessLog.operatingSystem, accessLog.count());

		List<Map<String, Object>> result = new ArrayList<>();

		long total = 0;
		for (Tuple tuple : queryResult) {
			Long count = tuple.get(accessLog.count());
			total = total + count;
		}

		BigDecimal totalBd = new BigDecimal(total);

		for (Tuple tuple : queryResult) {
			String os = tuple.get(accessLog.operatingSystem);
			Long count = tuple.get(accessLog.count());
			Map<String, Object> row = new HashMap<>();
			row.put("name", os);
			row.put("value", count);
			row.put("percent", new BigDecimal(count * 100).divide(totalBd, 2,
					BigDecimal.ROUND_HALF_UP));
			result.add(row);
		}

		return result;
	}

	@ExtDirectMethod
	@PreAuthorize("hasRole('ADMIN')")
	public void addTestData() {
		if (!environment.acceptsProfiles("default")) {

			String[] users = { "admin@starter.com", "user1@starter.com",
					"user2@starter.com", "user3@starter.com", "user4@starter.com",
					"user5@starter.com", "user6@starter.com" };
			String[] userAgent = {
					"Mozilla/5.0 (Windows NT 6.3; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/37.0.2062.103 Safari/537.36",
					"Mozilla/5.0 (Windows NT 6.3; WOW64; rv:32.0) Gecko/20100101 Firefox/32.0Mozilla/5.0 (Windows NT 6.3; WOW64; rv:25.0) Gecko/20100101 Firefox/25.0",
					"Mozilla/5.0 (Windows NT 6.3; WOW64; Trident/7.0; rv:11.0) like Gecko",
					"Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/28.0.1500.52 Safari/537.36 OPR/15.0.1147.100",
					"Mozilla/5.0 (Macintosh; Intel Mac OS X 10_9_0) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/32.0.1664.3 Safari/537.36",
					"Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/536.5 (KHTML, like Gecko) Chrome/19.0.1084.9 Safari/536.5",
					"Mozilla/5.0 (iPad; CPU OS 6_0 like Mac OS X) AppleWebKit/536.26 (KHTML, like Gecko) Version/6.0 Mobile/10A5355d Safari/8536.25" };
			String[] ipAddresses = { "64.4.11.42", "173.194.113.145", "31.13.93.129",
					"23.67.141.15", "54.225.87.37" };
			String[] locations = { "Redmond,United States",
					"Mountain View,United States", "Ireland", "Amsterdam,Netherlands",
					"Ashburn,United States" };

			Random random = new Random();
			int currentYear = LocalDateTime.now().getYear();
			UserAgentStringParser parser = UADetectorServiceFactory
					.getResourceModuleParser();

			List<AccessLog> accessLogs = new ArrayList<>();
			for (int i = 0; i < 1000; i++) {
				AccessLog log = new AccessLog();
				int rnd = random.nextInt(users.length);
				log.setEmail(users[rnd]);

				String ua = userAgent[rnd];
				log.setUserAgent(ua);
				ReadableUserAgent agent = parser.parse(ua);
				log.setUserAgentName(agent.getName());
				log.setUserAgentVersion(agent.getVersionNumber().getMajor());
				log.setOperatingSystem(agent.getOperatingSystem().getFamilyName());

				log.setSessionId(String.valueOf(i));

				LocalDateTime logIn = LocalDateTime.of(currentYear - random.nextInt(2),
						random.nextInt(12) + 1, random.nextInt(28) + 1,
						random.nextInt(24), random.nextInt(60), random.nextInt(60));
				log.setLogIn(logIn);

				int l = random.nextInt(ipAddresses.length);
				log.setIpAddress(ipAddresses[l]);
				log.setLocation(locations[l]);

				accessLogs.add(log);
			}

			accessLogRepository.save(accessLogs);
		}
	}

}
