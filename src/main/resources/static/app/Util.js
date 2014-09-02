Ext.define('Starter.Util', {
	singleton: true,

	successToast: function(msg) {
		Ext.toast({
			html: msg,
			title: i18n.successful,
			align: 't',
			shadow: true,
			width: 200,
			slideInDuration: 100,
			hideDuration: 100,
			bodyStyle: {
				background: 'lime',
				textAlign: 'center',
				fontWeight: 'bold'
			}
		});
	},

	errorToast: function(msg) {
		Ext.toast({
			html: msg,
			title: i18n.error,
			align: 't',
			shadow: true,
			width: 200,
			slideInDuration: 100,
			hideDuration: 100,
			bodyStyle: {
				background: 'red',
				color: 'white',
				textAlign: 'center',
				fontWeight: 'bold'
			}
		});
	},
	
	underline: function(str, char) {
		var pos = str.indexOf(char);
		if (pos !== -1) {
			return str.substring(0, pos) + '<u>' + char + '</u>' + str.substring(pos+1);
		}
		return str;
	}

});