Ext.define("Start.model.User",
{
  extend : "Ext.data.Model",
  requires : [ "Ext.data.proxy.Direct", "Ext.data.validator.Email", "Ext.data.validator.Length", "Ext.data.validator.Presence" ],
  fields : [ {
    name : "lastName",
    type : "string",
    validators : [ {
      type : "length",
      min : 0,
      max : 255
    } ]
  }, {
    name : "firstName",
    type : "string",
    validators : [ {
      type : "length",
      min : 0,
      max : 255
    } ]
  }, {
    name : "email",
    type : "string",
    validators : [ {
      type : "email"
    }, {
      type : "length",
      min : 0,
      max : 255
    }, {
      type : "presence"
    } ]
  }, {
    name : "role",
    type : "string"
  }, {
    name : "passwordNew",
    type : "string"
  }, {
    name : "passwordNewConfirm",
    type : "string"
  }, {
    name : "oldPassword",
    type : "string"
  }, {
    name : "locale",
    type : "string",
    validators : [ {
      type : "length",
      min : 0,
      max : 8
    } ]
  }, {
    name : "enabled",
    type : "boolean"
  }, {
    name : "id",
    type : "integer",
    allowNull : true,
    convert : null
  } ],
  proxy : {
    type : "direct",
    api : {
      read : "userService.read",
      create : "userService.create",
      update : "userService.update",
      destroy : "userService.destroy"
    },
    reader : {
      rootProperty : "records"
    },
    writer : {
      writeAllFields : true
    }
  }
});