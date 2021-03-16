/**
 * Created by Administrator on 2020/3/3.
 */
var JypurposeStore = Ext.create("Ext.data.Store", {
    fields: ['configid', 'value'],
    autoLoad: true,
    proxy: {
        type: 'ajax',
        url: '/electron/getJypurpose',
        extraParams: {
        },
        reader: {
            type: 'json',
            rootProperty: 'content',
            totalProperty: 'totalElements'
        }
    }
});

var borrowcontentMode = Ext.create("Ext.data.Store",{
    fields:["text","value"],
    data:[
        {text:"文书",value:"文书"},
        {text:"退伍",value:"退伍"},
        {text:"婚姻",value:"婚姻"},
        {text:"其他",value:"其他"}
    ]
});

var certificatesTypeStore = Ext.create("Ext.data.Store",{
    fields:["text","value"],
    data:[
        {text:"身份证",value:"身份证"},
        {text:"护照",value:"护照"},
        {text:"驾驶证",value:"驾驶证"}
    ]
});

Ext.define('Reservation.view.ReservationFormItemView', {
    extend: 'Ext.form.Panel',
    xtype: 'reservationFormItemView',
    itemId: 'reservationFormItemViewId',
    region: 'center',
    autoScroll: true,
    fieldDefaults: {
        labelWidth: 120
    },
    layout: 'column',
    bodyPadding: 15,
    items: [
        {xtype: 'textfield', name: 'id', hidden: true},
        {
            columnWidth: .3,
            xtype: 'label',
            text: '温馨提示：红色外框表示输入非法数据！',
            style: {
                color: 'red',
                'font-size': '16px'
            },
            margin: '5 0 0 0'
        }, {
            columnWidth: .7,
            xtype: 'displayfield'
        }, {
            columnWidth: .47,
            xtype: 'textfield',
            itemId:'borrowmanId',
            fieldLabel: '预约者',
            name: 'borrowman',
            allowBlank: false,
            margin: '5 0 0 0'
        }, {
            columnWidth: .03,
            value:'<label style="color:#ff0b23;!important;">*</label>',
            margin: '5 0 0 1',
            xtype: 'displayfield'
        }, {
            columnWidth: .47,
            xtype: 'textfield',
            itemId:'borrowmantelId',
            fieldLabel: '联系电话',
            name: 'borrowmantel',
            // allowBlank: false,
            regex: /^((0\d{2,3})-)(\d{7,8})(-(\d{3,}))?$|0?1[3|4|5|8][0-9]\d{8}/,
            regexText: '请输入正确电话号码',
            margin: '5 0 0 0',
            listeners: {
                render: function(sender) {
                    new Ext.ToolTip({
                        target: sender.el,
                        trackMouse: true,
                        dismissDelay: 0,
                        anchor: 'buttom',
                        html: "请输入正确的11位手机号码和座机号码（格式：区号+座机号码+分机号码）!"
                    });
                }
            }
        }, {
            columnWidth: .03,
            // value:'<label style="color:#ff0b23;!important;">*</label>',
            margin: '5 0 0 1',
            xtype: 'displayfield'
        }, {
            columnWidth: .47,
            xtype: 'textfield',
            itemId:'borroworganId',
            fieldLabel: '工作单位',
            name: 'borroworgan',
            margin: '5 0 0 0'
        }, {
            columnWidth: .03,
            margin: '5 0 0 1',
            xtype: 'displayfield'
        }, {
            columnWidth: .47,
            xtype: 'numberfield',
            itemId:'borrowmantimeId',
            fieldLabel: '来馆人数',
            name: 'borrowmantime',
            // allowBlank: false,
            margin: '5 0 0 0'
        }, {
            columnWidth: .03,
            // value:'<label style="color:#ff0b23;!important;">*</label>',
            margin: '5 0 0 1',
            xtype: 'displayfield'
        }, {
            columnWidth: .47,
            xtype: 'combo',
            store:certificatesTypeStore,
            fieldLabel: '证件类型',
            name: 'certificatestype',
            itemId:'certificatestypeId',
            displayField: 'value',
            valueField: 'value',
            margin: '5 0 0 0',
            listeners: {
                afterrender: function (combo) {
                    var store = combo.getStore();
                    if (store.getCount() > 0) {
                        combo.select(store.getAt(0));
                    }
                }
            }
        },{
            columnWidth: .03,
            xtype: 'displayfield'
        },{
            columnWidth: .47,
            xtype: 'textfield',
            fieldLabel: '证件号',
            name: 'certificatenumber',
            itemId:'certificatenumberId',
            margin: '5 0 0 0'
        }, {
            columnWidth: .03,
            xtype: 'displayfield'
        }, {
            columnWidth: .47,
            xtype: 'textfield',
            name: 'lymode',
            itemId:'lymodeId',
            fieldLabel: '预约类型',
            editable: false,
            margin: '5 0 0 0'
        }, {
            columnWidth: .03,
            xtype: 'displayfield'
        },{
            columnWidth: .47,
            xtype: 'textfield',
            name:'djtime',
            itemId:'djtimeId',
            fieldLabel: '登记时间',
            format: 'Y-m-d H:i:s',
            editable: false,
            value:new Date().format('yyyy-MM-dd hh:mm:ss'),
            margin: '5 0 0 0'
        },{
            columnWidth: .03,
            xtype: 'displayfield'
        },{
            columnWidth: .47,
            xtype: 'datetimefield',
            name:'yytime',
            itemId:'yytimeId',
            fieldLabel: '预约时间',
            format: 'Y-m-d H:i:s',
            margin: '5 0 0 0',
            listeners: {
                'select': function (m, d){

                    //获取当前时间
                    var date = new Date();
                    var year = date.getFullYear();
                    var month = date.getMonth() + 1;
                    var day = date.getDate();
                    if (month < 10) {
                        month = "0" + month;
                    }
                    if (day < 10) {
                        day = "0" + day;
                    }
                    var nowDate = year + month + day;

                    //选中的日期
                    var chooseDate=d.getDate();
                    var chooseyear = d.getFullYear();
                    var choosemonth = d.getMonth() + 1;
                    var chooseday = d.getDate();
                    if (choosemonth < 10) {
                        choosemonth = "0" + choosemonth;
                    }
                    if (chooseday < 10) {
                        chooseday = "0" + chooseday;
                    }
                    var chooseDate = chooseyear + '' + choosemonth + '' + chooseday;

                    if(chooseDate<nowDate){//不能选择当前时间之前的日期
                        XD.msg('不能选择过去的时间预约');
                    }
                }
            }
        },{
            columnWidth: .03,
            xtype: 'displayfield'
        },{
            columnWidth: .47,
            xtype: 'combo',
            store: JypurposeStore,
            name: 'borrowmd',
            fieldLabel: '查档目的',
            displayField: 'value',
            valueField: 'value',
            margin: '5 0 0 0',
            listeners: {
                afterrender: function (combo) {
                    var store = combo.getStore();
                    if (store.getCount() > 0) {
                        combo.select(store.getAt(0));
                    }
                }
            }
        },{
            columnWidth: .03,
            value:'<label style="color:#ff0b23;!important;">*</label>',
            margin: '5 0 0 1',
            xtype: 'displayfield'
        }, {
            columnWidth: .32,
            xtype: 'textfield',
            itemId:'media',
            fieldLabel: '附件',
            editable: false,
            name: 'evidencetext',
            margin: '10 0 0 0'
        },{
            columnWidth: .08,
            style : 'text-align:center;',
            margin: '17 0 0 0',
            items: [
                {
                    xtype: 'label',
                    itemId:'mediacount',
                    text: '共0份'
                }
            ]
        }, {
            columnWidth: .07,
            margin: '10 0 0 0',
            items: [
                {
                    xtype: 'button',
                    itemId:'electronId',
                    text: '上传'
                }
            ]
        },{
            columnWidth: .03,
            // value:'<label style="color:#ff0b23;!important;">*</label>',
            margin: '5 0 0 1',
            xtype: 'displayfield'
        }, {
            columnWidth:.97,
            xtype: 'textarea',
            fieldLabel: '查档内容',
            name: 'borrowcontent',
            margin: '5 0 0 0'
        },{
            columnWidth: .03,
            margin: '10 0 0 1',
            xtype: 'displayfield'
        }],
    buttons: [
        {text: '提交', itemId: 'formSubmit'},
        {text: '关闭', itemId: 'formClose'}
    ]
});