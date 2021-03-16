/**
 * 温湿度设置组件
 * Created by wujy on 2019-09-02.
 */
Ext.define('Lot.view.EnvironmentSettingFormView',{
    extend:'Ext.form.FormPanel',
    xtype:'environmentSettingForm',
    bodyPadding:25,
    height:70,
    layout:'column',
    defaults:{
        xtype:'textfield'
    },
    items:[{
        columnWidth:.2,
        xtype:'textfield',
        itemId:'tem',
        labelWidth:70,
        fieldLabel:'温度(℃)'
    },{
        columnWidth:.1,
        xtype:'button',
        itemId:'temSetting',
        text:'设定'
    },{
        columnWidth:.2,
        itemId:'hum',
        xtype:'textfield',
        labelWidth:70,
        margin:'0 0 0 10',
        fieldLabel:'湿度(%)'
    },{
        columnWidth:.1,
        xtype:'button',
        itemId:'humSetting',
        text:'设定'
    },{
        width:100,
        xtype:'button',
        text:'运行',
        itemId:'start',
        margin:'0 0 0 30'
    },{
        width:100,
        xtype:'button',
        text:'停止',
        itemId:'stop'
    }],

    valueSetHidden : function(){
        this.down('[itemId=tem]').setHidden(true);
        this.down('[itemId=temSetting]').setHidden(true);
        this.down('[itemId=hum]').setHidden(true);
        this.down('[itemId=humSetting]').setHidden(true);
    },

    controlHidden : function(){
        this.down('[itemId=start]').setHidden(true);
        this.down('[itemId=stop]').setHidden(true);
    }

});