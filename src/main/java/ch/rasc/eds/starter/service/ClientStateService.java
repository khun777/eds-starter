package ch.rasc.eds.starter.service;

import static ch.ralscha.extdirectspring.annotation.ExtDirectMethodType.STORE_MODIFY;
import static ch.ralscha.extdirectspring.annotation.ExtDirectMethodType.STORE_READ;

import java.util.List;

import javax.persistence.EntityManager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.web.bind.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ch.ralscha.extdirectspring.annotation.ExtDirectMethod;
import ch.ralscha.extdirectspring.bean.ExtDirectStoreResult;
import ch.rasc.eds.starter.config.security.JpaUserDetails;
import ch.rasc.eds.starter.entity.ClientState;
import ch.rasc.eds.starter.entity.QClientState;

import com.mysema.query.jpa.impl.JPADeleteClause;
import com.mysema.query.jpa.impl.JPAQuery;

@Service
@PreAuthorize("isAuthenticated()")
public class ClientStateService {

	private final EntityManager entityManager;

	@Autowired
	public ClientStateService(EntityManager entityManager) {
		this.entityManager = entityManager;
	}

	@ExtDirectMethod(STORE_READ)
	@Transactional(readOnly = true)
	public List<ClientState> read(@AuthenticationPrincipal JpaUserDetails jpaUserDetails) {
		return new JPAQuery(entityManager).from(QClientState.clientState)
				.where(QClientState.clientState.userId.eq(jpaUserDetails.getUserDbId()))
				.list(QClientState.clientState);
	}

	@ExtDirectMethod(STORE_MODIFY)
	@Transactional
	public ExtDirectStoreResult<ClientState> destroy(
			@AuthenticationPrincipal JpaUserDetails jpaUserDetails,
			List<ClientState> states) {

		for (ClientState state : states) {
			new JPADeleteClause(entityManager, QClientState.clientState).where(
					QClientState.clientState.userId.eq(jpaUserDetails.getUserDbId()).and(
							QClientState.clientState.id.eq(state.getId()))).execute();
		}

		ExtDirectStoreResult<ClientState> result = new ExtDirectStoreResult<>();
		result.setSuccess(true);
		return result;
	}

	@ExtDirectMethod(STORE_MODIFY)
	@Transactional
	public List<ClientState> create(
			@AuthenticationPrincipal JpaUserDetails jpaUserDetails,
			List<ClientState> states) {

		for (ClientState state : states) {
			state.setId(null);
			state.setUserId(jpaUserDetails.getUserDbId());
			entityManager.persist(state);
		}

		return states;
	}

	@ExtDirectMethod(STORE_MODIFY)
	@Transactional
	public List<ClientState> update(
			@AuthenticationPrincipal JpaUserDetails jpaUserDetails,
			List<ClientState> states) {

		for (ClientState state : states) {
			state.setUserId(jpaUserDetails.getUserDbId());
			entityManager.merge(state);
		}

		return states;
	}

}
