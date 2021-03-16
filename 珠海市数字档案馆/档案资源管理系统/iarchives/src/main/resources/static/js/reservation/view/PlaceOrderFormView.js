/**
 * Created by Administrator on 2020/4/21.
 */

Ext.define('Reservation.view.PlaceOrderFormView', {
    extend: 'Ext.window.Window',
    xtype: 'placeOrderFormView',
    itemId: 'placeOrderFormViewId',
    autoScroll: true,
    width: '70%',
    height: '50%',
    draggable: false,//禁止拖动
    resizable: false,//禁止缩放
    modal: true,
    closeToolText:'关闭',
    title: '',
    closeAction: 'hide',
    layout: 'fit',
    items: [{
        xtype: 'form',
        layout: 'column',
        autoScroll: true,
        bodyPadding: 16,
        fieldDefaults: {
            labelWidth: 70
        },
        items: [
            {
                columnWidth: .3,
                xtype: 'label',
                text: '温馨提示：红色外框表示输入非法数据！',
                style: {
                    color: 'red',
                    'font-size': '16px'
                },
                margin: '10 0 0 0'
            }, {
                columnWidth: .6,
                xtype: 'displayfield'
            }, {
                columnWidth: .47,
                fieldLabel: '预约人',
                itemId:'placeuserId',
                xtype: 'textfield',
                name: 'placeuser',
                labelWidth: 85,
                allowBlank: false,
                margin: '10 0 0 0',
                afterLabelTextTpl: [
                    '<span style="color:red;font-weight:bold" data-qtip="必填项">*</span>']
            },{
                columnWidth:.06,
                xtype:'displayfield'
            },{
                columnWidth: .47,
                fieldLabel: '单位',
                xtype: 'textfield',
                name: 'userorgan',
                labelWidth: 85,
                allowBlank: false,
                margin: '10 0 0 0',
                afterLabelTextTpl: [
                    '<span style="color:red;font-weight:bold" data-qtip="必填项">*</span>']
            },{
                columnWidth: .47,
                fieldLabel: '联系电话',
                xtype: 'textfield',
                name: 'phonenumber',
                labelWidth: 85,
                allowBlank: false,
                margin: '10 0 0 0',
                regex :/(^1[0-9]{10}$)/,
                regexText   : "电话格式不正确！",
                afterLabelTextTpl: [
                    '<span style="color:red;font-weight:bold" data-qtip="必填项">*</span>']
            },{
                columnWidth:.06,
                xtype:'displayfield'
            },{
                columnWidth: .47,
                xtype: 'textfield',
                itemId: 'usewayId',
                name: 'useway',
                fieldLabel: '使用用途',
                labelWidth: 85,
                allowBlank: false,
                margin: '10 0 0 0',
                afterLabelTextTpl: [
                    '<span style="color:red;font-weight:bold" data-qtip="必填项">*</span>']
            },{
                columnWidth: .47,
                fieldLabel: '预约时间',
                name: 'ordertime',
                xtype: 'textfield',
                labelWidth: 85,
                allowBlank: false,
                editable: false,
                margin:'10 0 0 0',
                afterLabelTextTpl: [
                    '<span style="color:red;font-weight:bold" data-qtip="必填项">*</span>']
            },{
                columnWidth:.06,
                xtype:'displayfield'
            },{
                columnWidth: .23,
                fieldLabel: '开始时间',
                xtype: 'datetimefield',
                name: 'starttime',
                format: 'Y-m-d H:i',
                labelWidth: 85,
                allowBlank: false,
                editable: false,
                margin:'10 0 0 0'
            }, {
                columnWidth:.01,
                xtype:'displayfield'
            },{
                columnWidth: .23,
                fieldLabel: '结束时间',
                xtype: 'datetimefield',
                format: 'Y-m-d H:i',
                name: 'endtime',
                labelWidth: 85,
                editable: false,
                margin:'10 0 0 0'
            },{
                columnWidth: .47,
                xtype: 'combo',
                itemId: 'auditlinkId',
                store: 'PlaceOrderNodeStore',
                queryMode:'local',
                fieldLabel: '审核环节',
                labelWidth: 85,
                displayField: 'text',
                valueField: 'id',
                allowBlank: false,
                editable: false,
                margin: '10 0 0 0',
                listeners: {
                    beforerender: function (combo) {
                        combo.getStore().on('load',function () {
                            var store = combo.getStore();
                            if(store.getCount()>0){
                                var record = store.getAt(0);
                                combo.select(record);
                                combo.fireEvent("select",combo,record);
                            }
                        });
                    },
                    select:function (view,record) {
                        var spmancombo = view.findParentByType("placeOrderFormView").down("[itemId=spmanId]");
                        spmancombo.select(null);
                        var spmanStore = spmancombo.getStore();
                        spmanStore.proxy.extraParams.nodeId = record.get('id');
                        spmanStore.load();
                    }
                }
            },{
                columnWidth: .06,
                xtype: 'displayfield'
            },{
                columnWidth: .47,
                xtype: 'combo',
                itemId: 'spmanId',
                store: 'ApproveManStore',
                queryMode:'local',
                fieldLabel: '审核人',
                labelWidth: 85,
                displayField: 'realname',
                valueField: 'userid',
                allowBlank: false,
                editable: false,
                margin: '10 0 0 0',
                listeners: {
                    beforerender: function (combo) {
                        var store = combo.getStore();
                        store.on("load",function () {
                            if(store.getCount()>0){
                                var record = store.getAt(0);
                                combo.select(record);
                                combo.fireEvent("select",combo,record);
                            }
                        });
                    }
                }
            },{
                columnWidth: 1,
                xtype: 'textarea',
                fieldLabel: '备注',
                labelWidth: 85,
                name: 'remark',
                margin: '10 0 0 0'
            }
        ]
    }],
    buttons: [
        {text: '提交', itemId: 'placeOrderSubmit'},
        {text: '关闭', itemId: 'placeOrderClose'}
    ]
});
