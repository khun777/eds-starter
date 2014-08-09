Ext.define("Start.model.AccessLog",
{
  extend : "Ext.data.Model",
  requires : [ "Ext.data.proxy.Direct", "Ext.data.validator.Length" ],
  fields : [ {
    name : "userName",
    type : "string",
    validators : [ {
      type : "length",
      min : 0,
      max : 255
    } ]
  }, {
    name : "logIn",
    type : "date",
    dateFormat : "c"
  }, {
    name : "logOut",
    type : "date",
    dateFormat : "c"
  }, {
    name : "duration",
    type : "string"
  }, {
    name : "userAgentName",
    type : "string",
    validators : [ {
      type : "length",
      min : 0,
      max : 20
    } ]
  }, {
    name : "userAgentVersion",
    type : "string",
    validators : [ {
      type : "length",
      min : 0,
      max : 10
    } ]
  }, {
    name : "operatingSystem",
    type : "string",
    validators : [ {
      type : "length",
      min : 0,
      max : 20
    } ]
  }, {
    name : "id",
    type : "integer",
    allowNull : true,
    convert : null
  } ],
  proxy : {
    type : "direct",
    directFn : "accessLogService.read",
    reader : {
      rootProperty : "records"
    },
    writer : {
      writeAllFields : true
    }
  }
});