/**
 * Created by tanly on 2017/12/11 0011.
 */
Ext.define('ArchivesMigrate.view.ArchivesMigrateFormView', {
    extend: 'Ext.window.Window',
    xtype: 'archivesMigrateFormView',
    itemId: 'archivesMigrateFormViewId',
    title: '迁移',
    width: 780,
    height: 410,
    modal: true,
    closeToolText:'关闭',
    layout: 'fit',
    items: [{
        layout: {
            type: 'vbox',
            align: 'stretch'
        },
        xtype: 'form',
        itemId: 'formitemid',
        margin: '22',
        items: [{
            xtype:'textfield',
            fieldLabel: '',
            name: 'migid',
            hidden: true
        },{
            xtype:'textarea',
            fieldLabel: '内容描述',
            name: 'migratedesc',
            itemId: 'migratedescId'
        }, {
            layout: 'column',
            itemId:'multcolumnId',
            items: [{
                columnWidth: .47,
                items: [{
                    xtype: 'textfield',
                    fieldLabel: '迁移人',
                    itemId: 'migrateuserId',
                    name: 'migrateuser',
                    style: 'width: 100%'
                }]
            },{
                columnWidth:.06,
                xtype:'displayfield',
            },{
                columnWidth: .47,
                items: [{
                    xtype: 'textfield',
                    fieldLabel: '迁移电子档案数',
                    name: 'migratecount',
                    itemId:'migratecountId',
                    style: 'width: 100%'
                }]
            },{
                columnWidth: .47,
                items: [{
                    xtype: 'textfield',
                    fieldLabel: '迁移状态',
                    name: 'migratestate',
                    itemId: 'migratestateId',
                    editable: false,
                    style: 'width: 100%'
                }]
            },{
                columnWidth:.06,
                xtype:'displayfield',
                itemId:'displayfield',
            },{
                columnWidth: .47,
                items: [{
                    fieldLabel: '迁移时间',
                    xtype: 'textfield',
                    name: 'migratedate',
                    format: 'Y-m-d H:i:s',
                    style: 'width: 100%',
                    editable: false,
                    value:new Date().format('yyyy-MM-dd hh:mm:ss')
                }]
            }]
        },{
            xtype:'textarea',
            fieldLabel: '备注',
            name: 'remarks',
            itemId: 'remarksid'
        }]
    }],
    buttons: [{
        text: '新增',
        itemId: 'saveId'
    }, {
        text: '取消',
        itemId: 'CancelBtnID'
    }]
});