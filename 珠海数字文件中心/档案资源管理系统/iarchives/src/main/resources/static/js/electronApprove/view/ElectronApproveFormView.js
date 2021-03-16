/**
 * Created by RonJiang on 2017/10/31 0031.
 */
Ext.define('ElectronApprove.view.ElectronApproveFormView',{
    extend: 'Ext.form.Panel',
    xtype: 'electronApproveFormView',
    itemId:'electronApproveFormViewId',
    region: 'center',
    title:'审核单据',
    autoScroll: true,
    fieldDefaults: {
        labelWidth: 70
    },
    layout:'column',
    bodyPadding: 15,
    items:[{
    	xtype: 'textfield',
    	name:'id',
    	hidden:true
    },{
        xtype: 'textfield',
        name:'datasourcetype',
        itemId:"datasourcetypeId",
        hidden:true
    },{
        columnWidth: .47,
        xtype: 'textfield',
        fieldLabel: '申办人',
        labelWidth: 85,
        name:'borrowman',
        margin:'10 0 0 0',
        readOnly:true
    },{
        columnWidth:.06,
        xtype:'displayfield'
    },{
		columnWidth: .47,
        xtype: 'textfield',
        fieldLabel: '查档内容',
        labelWidth: 85,
        name:'borrowcontent',
        margin:'10 0 0 0',
        readOnly:true
	},{
        columnWidth: .23,
        xtype: 'textfield',
        fieldLabel: '提供证件',
        labelWidth: 85,
        name:'certificatetype',
        readOnly:true,
        margin:'10 0 0 0'
    },{
        columnWidth: .01,
        xtype: 'displayfield'
    },{
        columnWidth: .23,
        fieldLabel: '证件号码',
        xtype: 'textfield',
        name: 'certificatenumber',
        labelWidth: 85,
        readOnly:true,
        margin: '10 0 0 0'
    },{
        columnWidth: .06,
        xtype: 'displayfield'
    },{
        columnWidth: .47,
        xtype: 'textfield',
        itemId: 'borrowmdId',
        name: 'borrowmd',
        fieldLabel: '目的',
        labelWidth: 85,
        readOnly:true,
        margin: '10 0 0 0'
    },{
        columnWidth: .47,
        fieldLabel: '地址',
        xtype: 'textfield',
        name: 'comaddress',
        labelWidth: 85,
        readOnly:true,
        margin: '10 0 0 0'
    }, {
        columnWidth:.06,
        xtype:'displayfield'
    },{
        columnWidth: .47,
        fieldLabel: '电话或者电子邮箱',
        xtype: 'textfield',
        name: 'borrowmantel',
        labelWidth: 85,
        readOnly:true,
        margin: '10 0 0 0'
    },{
        columnWidth: .23,
        fieldLabel: '与当事人的关系',
        xtype: 'textfield',
        name: 'relationship',
        labelWidth: 85,
        readOnly:true,
        margin: '-7 0 0 0'
    },{
        columnWidth:.01,
        xtype:'displayfield'
    },{
        columnWidth: .23,
        fieldLabel: '查档（接收）单位',
        xtype: 'textfield',
        name: 'borroworgan',
        labelWidth: 115,
        readOnly:true
    },{
        columnWidth:.06,
        xtype:'displayfield'
    },{
        columnWidth: .47,
        fieldLabel: '利用方式',
        xtype: 'textfield',
        name: 'lymode',
        labelWidth: 85,
        readOnly:true,
        margin: '10 0 0 0'
    },{
        columnWidth: .23,
        fieldLabel: '申请时间',
        xtype: 'textfield',
        name: 'borrowdate',
        labelWidth: 85,
        readOnly:true,
        margin:'10 0 0 0'
    },{
        columnWidth:.01,
        xtype:'displayfield'
    },{
        columnWidth: .23,
        fieldLabel: '受理时间',
        xtype: 'textfield',
        name: 'acceptdate',
        labelWidth: 85,
        margin:'10 0 0 0',
        readOnly:true
    }, {
        columnWidth: .06,
        xtype: 'displayfield'
    },{
        columnWidth: .23,
        xtype: 'textfield',
        itemId:'borrowtytsId',
        fieldLabel: '同意查档天数',
        labelWidth: 85,
        name: 'borrowtyts',
        allowBlank: false,
        margin: '10 0 0 0'
    },{
        columnWidth:.01,
        xtype:'displayfield'
    },{
        columnWidth: .23,
        xtype: 'textfield',
        itemId:'borrowtsId',
        fieldLabel: '查档天数',
        labelWidth: 85,
        name: 'borrowts',
        allowBlank: false,
        margin: '10 0 0 0'
    },{
        columnWidth: .47,
        xtype: 'textfield',
        itemId:'borrowmantimeld',
        fieldLabel: '利用人数',
        labelWidth: 85,
        name: 'borrowmantime',
        readOnly:true,
        margin: '10 0 0 0'
    },{
        columnWidth: .06,
        itemId:'notext',
        xtype: 'displayfield'
    },{
        columnWidth: .32,
        xtype: 'textfield',
        itemId:'media',
        fieldLabel: '附件',
        labelWidth: 85,
        readOnly:true,
        name: 'evidencetext',
        margin: '10 0 0 0'
    },{
        columnWidth: .08,
        style : 'text-align:center;',
        margin: '17 0 0 0',
        items: [{
            xtype: 'label',
            itemId:'mediacount',
            text: '共0份',
            listeners:{
                render:function (view) {
                    Ext.Ajax.request({
                        url: '/electronApprove/getEvidencetextCount',
                        params:{
                            taskid:taskid
                        },
                        success: function (response) {
                            var text = Ext.decode(response.responseText).data;
                            view.setText('共'+text+'份');
                        }
                    });
                }
            }
        }]
    },{
        columnWidth: .07,
        margin: '10 0 0 0',
        items: [{
            xtype: 'button',
            itemId:'electronId',
            text: '查看'
        }]
    },{
        layout : 'column',
        xtype:'fieldset',
        style:'background:#fff;padding-top:0px',
        columnWidth:1,
        title: '其它字段',
        collapsible: true,
        collapsed:false,
        autoScroll: false,
        items:[{
            columnWidth: 0.47,
            xtype: 'textfield',
            fieldLabel: '复制内容',
            labelWidth: 85,
            name: 'copycontent',
            margin: '5 0 0 0',
            readOnly:true
        },{
            columnWidth:.06,
            xtype:'displayfield'
        },{
            columnWidth: 0.47,
            xtype: 'textfield',
            fieldLabel: '复制目的',
            labelWidth: 85,
            name: 'copymd',
            margin: '5 0 0 0',
            readOnly:true
        }]
    },{
	    columnWidth: 1,
	    xtype: 'textfield',
	    fieldLabel: '备注',
	    labelWidth: 85,
	    name:'desci',
	    margin: '5 0 0 0',
	    height:30,//文本框默认高度为30
	    readOnly:true
    },{
        columnWidth: 1,
        itemId:'approveId',
        xtype: 'textarea',
        fieldLabel: '批示',
        labelWidth: 85,
        name:'approve',
        flex: 1,
        margin: '5 0 0 0',
        readOnly:true
       // disabled:true
    }],
    buttons:[{
        xtype: 'checkbox',
        itemId:'sendmsgId',
        inputValue : true,
        fieldLabel:'发送短信',
        margin: '0 30 0 0',
        hidden:borrrowSendmsg==true?false:true
    },{
    	text: '添加批示',
    	itemId:'approveAdd'
    }, {
        xtype : 'combo',
        store : 'NextNodeStore',
        itemId:'nextNodeId',
        name:'spman',
        fieldLabel: '下一环节',
        labelWidth: 85,
        displayField : 'text',
        editable:false,
        valueField : 'id',
        queryMode:'local',
        style: "margin-left:24px",
        listeners:{
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
                var spmanOrgan = combo.findParentByType("electronApproveFormView").down("[itemId=approveOrgan]");
                spmanOrgan.select(null);
                var nextSpman = combo.findParentByType("electronApproveFormView").down("[itemId=nextSpmanId]");
                nextSpman.select(null);
                nextSpman.getStore().removeAll();
                nextSpman.getStore().proxy.extraParams.nodeId = record.get('id');
                spmanOrgan.getStore().proxy.extraParams.type = "approve"; //审批时获取审批单位
                spmanOrgan.getStore().proxy.extraParams.taskid = taskid;
                spmanOrgan.getStore().proxy.extraParams.nodeid = record.get('id');
                spmanOrgan.getStore().proxy.extraParams.worktext = null;
                spmanOrgan.getStore().proxy.extraParams.approveType = "borrow"; //审批类型
                spmanOrgan.getStore().reload(); //刷新审批单位
            }
        }
    },{
        xtype : 'combo',
        store : 'ApproveOrganStore',
        itemId:'approveOrgan',
        fieldLabel: '审批单位',
        displayField : 'organname',
        queryMode:'local',
        editable:false,
        valueField : 'organid',
        listeners:{
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
                var spmancombo = combo.findParentByType("electronApproveFormView").down("[itemId=nextSpmanId]");
                spmancombo.select(null);
                var spmanStore = spmancombo.getStore();
                spmanStore.proxy.extraParams.organid = record.get("organid");
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
        xtype : 'combo',
        store : 'NextSpmanStore',
        itemId:'nextSpmanId',
        name:'spman',
        fieldLabel: '审批人',
        displayField : 'realname',
        queryMode:'local',
        editable:false,
        valueField : 'userid',
        listeners:{
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
    	text: '完成',
    	itemId:'electronApproveFormSubmit'
    }, {
    	text: '退回',
    	itemId:'electronApproveFormZz'
    },{
        text: '查无此档',
        itemId:'electronApproveFormFn'
    },{
    	text: '关闭',
    	itemId:'electronApproveFormClose'
    }]
});