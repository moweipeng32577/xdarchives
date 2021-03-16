/**
 * Created by RonJiang on 2018/04/23
 */
Ext.define('Recyclebin.view.RecyclebinFormView',{
    extend:'Ext.form.Panel',
    xtype:'recyclebinform',
    layout:'column',
    defaults:{
        layout:'form',
        xtype:'textfield',
        labelWidth: 120,
        labelSeparator:'：'
    },
    items:[{
        columnWidth:.48,
        xtype : 'textfield',
        fieldLabel:'文件名称',
        name:'filename',
        margin:'30 1 10 20',
        readOnly:true
    },{
        columnWidth: .02,
        xtype : 'displayfield'
    },{
        columnWidth:.48,
        xtype : 'textfield',
        fieldLabel:'文件类型',
        name:'filetype',
        margin:'30 1 10 20',
        readOnly:true
    },{
        columnWidth: .02,
        xtype : 'displayfield'
    },{
        columnWidth:.48,
        xtype : 'textfield',
        fieldLabel:'文件路径',
        name:'filepath',
        margin:'30 1 10 20',
        readOnly:true
    },{
        columnWidth: .02,
        xtype : 'displayfield'
    },{
        columnWidth:.48,
        xtype : 'textfield',
        fieldLabel:'文件大小',
        name:'filesize',
        margin:'30 1 10 20',
        readOnly:true
    },{
        columnWidth: .02,
        xtype : 'displayfield'
    },{
        columnWidth:.48,
        xtype : 'textfield',
        fieldLabel:'删除时间',
        name:'deletetime',
        margin:'30 1 10 20',
        readOnly:true
    },{
        columnWidth: .02,
        xtype : 'displayfield'
    },{
        columnWidth:.48,
        xtype : 'textfield',
        fieldLabel:'源数据表',
        name:'originaltable',
        margin:'30 1 10 20',
        readOnly:true
    },{
        columnWidth: .02,
        xtype : 'displayfield'
    }],

    buttons:[{
        text:'返回',
        itemId:'back'
    }]
});