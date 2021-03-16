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
        {text:"查01门类的档案",value:"查01门类的档案"},
        {text:"查02门类的档案",value:"查02门类的档案"},
        {text:"查03门类的档案",value:"查03门类的档案"},
        {text:"查04门类的档案",value:"查04门类的档案"},
        {text:"查05门类的档案",value:"查05门类的档案"}
    ]
});

Ext.define('Reservation.view.ExhibitionFormItemView', {
    extend: 'Ext.form.Panel',
    xtype: 'exhibitionFormItemView',
    itemId: 'exhibitionFormItemViewId',
    layout:'border',

    items:[{
        //region: 'center',
        region:'north',
        autoScroll: true,
        xtype: 'form',
        itemId: 'exhibitionFormId',
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
                xtype: 'textfield',
                fieldLabel: '身份证号',
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
                name:'yystate',
                itemId:'yystateId',
                fieldLabel: '状态',
                value:'未回复',
                margin: '5 0 0 0'
            },{
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
                        var store = m.up('exhibitionFormItemView').down('[itemId=showroomGrid]').getStore();
                        store.proxy.extraParams.date = d;//预约日期
                        store.reload();
                    }
                }
            },{
                columnWidth: .03,
                value:'<label style="color:#ff0b23;!important;">*</label>',
                margin: '5 0 0 1',
                xtype: 'displayfield'
            }]
        },{
            region:'center',
            xtype:'basicgrid',
            itemId:'showroomGrid',
            store:'ShowroomGridStore',
            hasSearchBar:false,
            tbar:[{
                text : '查看展厅',itemId:'lookShowroomid'
            }/*,{text:'删除',itemId:'delTemp'}*/],
            margin:'5 5 5 5',
            columns: [
                {text: '展厅名称', dataIndex: 'title', flex: 2, menuDisabled: true},
                {text: '展厅介绍', dataIndex: 'content', flex: 4, menuDisabled: true,renderer: function(value, cellmeta, record) {
                    var reTag = /<(?:.|\s)*?>/g;
                    return value.replace(reTag,"");
                } },
                {text: '展厅附件', dataIndex: 'appendix', flex:2, menuDisabled: true},
                {text: '展厅状态', dataIndex: 'flag', flex: 1, menuDisabled: true,renderer: function(value, cellmeta, record) {
                    if(value.indexOf('1') > -1){
                        return "<span style=\"color:blue\">已满</span>"
                    }else if(value.indexOf('0') > -1){
                        return "<span style=\"color:green\">正常</span>"
                    }else{
                        return "<span style=\"color:red\">维护中</span>"
                    }
                }},
                {text: '当天已预约人数', dataIndex: 'yyAudiences', flex:1, menuDisabled: true},
                {text: '每日参观人数限制', dataIndex: 'audiences', flex:1, menuDisabled: true}
            ]
        }],

    buttons: [
        {text: '提交', itemId: 'formSubmit'},
        {text: '关闭', itemId: 'formClose'}
    ]
});