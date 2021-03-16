/**
 * DateField定制
 * 支持各种输入格式，如2019
 */
Ext.define('Ext.ux.MutiDateField', {

    xtype:'mutidatefield',

    extend:'Ext.form.field.Date',

    onBlur:function(e){
        this.rawDate = this.rawDateText;
        this.value = this.rawDate;
    },

    getSubmitValue:function(){
        return this.value;
    }

});

