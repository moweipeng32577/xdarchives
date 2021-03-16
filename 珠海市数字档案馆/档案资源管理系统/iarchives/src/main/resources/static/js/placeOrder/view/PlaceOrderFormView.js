/**
 * Created by Administrator on 2020/4/21.
 */

Ext.define('PlaceOrder.view.PlaceOrderFormView', {
    extend: 'Ext.window.Window',
    xtype: 'placeOrderFormView',
    itemId: 'placeOrderFormViewId',
    autoScroll: true,
    width: '70%',
    height: '60%',
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
                columnWidth: .5,
                xtype: 'label',
                text: '温馨提示：红色外框表示输入非法数据！',
                style: {
                    color: 'red',
                    'font-size': '16px'
                },
                margin: '10 0 0 0'
            }, {
                columnWidth: .5,
                xtype: 'displayfield'
            }, {
                columnWidth: .47,
                fieldLabel: '预约人',
                itemId:'placeuserId',
                xtype: 'textfield',
                name: 'placeuser',
                labelWidth: 85,
                allowBlank: false,
                editable: orderAuditState=='true'?true:false,
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
                editable: orderAuditState=='true'?true:false,
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
                margin:'10 0 0 0',
                renderSecondBtnStr:false
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
                margin:'10 0 0 0',
                renderSecondBtnStr:false
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
                        var store = combo.getStore();
                        store.on("load",function () {
                            if(store.getCount()>0){
                                var record = store.getAt(0);
                                combo.select(record);
                                combo.fireEvent("select",combo,record);
                            }
                        });
                    },
                    select:function (combo,record) {
                        var spmanOrgan = combo.findParentByType("placeOrderFormView").down("[itemId=approveOrgan]");
                        spmanOrgan.select(null);
                        var spman = combo.findParentByType("placeOrderFormView").down("[itemId=spmanId]");
                        spman.select(null);
                        spman.getStore().removeAll();
                        spman.getStore().proxy.extraParams.nodeId = record.get('id');
                        spmanOrgan.getStore().proxy.extraParams.type = "submit"; //审批时获取审批单位
                        spmanOrgan.getStore().proxy.extraParams.taskid = null;
                        spmanOrgan.getStore().proxy.extraParams.nodeid = record.get('id');
                        spmanOrgan.getStore().proxy.extraParams.worktext = null;
                        spmanOrgan.getStore().proxy.extraParams.approveType = "placeOrder"; //审批类型
                        spmanOrgan.getStore().reload(); //刷新审批单位
                    }
                }
            },{
                columnWidth: .06,
                xtype: 'displayfield'
            },{
                columnWidth: .23,
                xtype: 'combobox',
                itemId:'approveOrgan',
                store: 'ApproveOrganStore',
                fieldLabel: '审批单位',
                labelWidth: 85,
                queryMode: "local",
                allowBlank: false,
                displayField: 'organname',
                valueField: 'organid',
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
                    select:function (combo,record) {
                        var spmancombo = combo.findParentByType("placeOrderFormView").down("[itemId=spmanId]");
                        spmancombo.select(null);
                        var spmanStore = spmancombo.getStore();
                        spmanStore.proxy.extraParams.findOrganid = record.get("organid");
                        spmanStore.reload();
                    },
                    render: function(sender) {
                        new Ext.ToolTip({
                            target: sender.el,
                            trackMouse: true,
                            dismissDelay: 0,
                            anchor: 'buttom',
                            html: ' <i class="fa fa-info-circle"></i> '+"支持跨单位查档申请，请选择要查档的单位！"
                        });
                    }
                }
            },{
                columnWidth: .01,
                xtype: 'displayfield'
            },{
                columnWidth: .23,
                xtype: 'combo',
                itemId: 'spmanId',
                store: 'ApproveManStore',
                queryMode:'local',
                fieldLabel: '审核人',
                labelWidth: 85,
                displayField: 'realname',
                valueField: 'userid',
                queryMode: "local",
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
