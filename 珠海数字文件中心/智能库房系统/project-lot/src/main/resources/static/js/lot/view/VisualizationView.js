/**
 * Created by Rong on 2019-01-16.
 */
var pageSize=25;
Ext.define('Lot.view.VisualizationView',{
    extend:'Ext.tab.Panel',
    xtype:'visualization',
    frame:false,
    activeTab:0,
    items:[{
        title:'总览',
        itemId:'totalPanel',
        layout:'border',
        items:[{
            region:'center',
            layout:'fit',
            items:[{
                width:1290,
                height:745,
                scrollable:true,
                itemId:'plane',
                layout:'absolute',
                bodyStyle:{
                    'background-repeat':'no-repeat',
                    'background-size':'contain'
                }
            }]
        },{
            region:'west', width:300, xtype:'devicelist'
        }]
    },{
        title:'库房环境',
        itemId:'envPanel',
        bodyPadding:15,
        layout:'border'
    },{
        title:'库房安全',
        itemId:'securityPanel',
        layout:'border',
        items:[
            {region:'center', xtype:'jkframe'},
            {region:'west', width:300, xtype:'JKlist'}
            ]
    }, {
        title:'区域管理',
        itemId:'areaPanel',
        xtype:'panel',
        bodyPadding:'10',
        layout:'border',
        items:[{
            region:'west',
            xtype:'panel',
            width: 500,
            margin:'10',
            items:[{
                region:'north',
                height:400,
                title:'楼层管理',
                xtype:'DeviceFloorGridView'
                }, {
                region:'south',
                title:'区域管理',
                xtype:'DeviceAreaGridView'
            }]
        },{
            region:'center',
            margin:'30',
            itemId: 'itemselectorID',
            xtype: 'itemselector',
            imagePath: '../ux/images/',
            store: 'DeviceSeletorStore',
            displayField: 'name',
            valueField: 'id',
            msgTarget: 'side',
            fromTitle: '可选设备',
            toTitle: '已选设备'
        }],
        buttons: [
            { text: '提交',itemId:'seletorSubmit'},
            { text: '清空',itemId:'close'}
        ]
    },{
        title:'设备管理',
        itemId:'devicePanel',
        xtype:'panel',
        bodyPadding:'10',
        layout:'border',
        items:[{
            region:'west',
            title:'设备类型',
            width:400,
            margin:20,
            xtype:'DeviceTypeView'
        },{
            region:'center',
            title:'设备列表',
            xtype:'DeviceGridView'
        }]
    },{
        title:'设备联动管理',
        itemId:'eventPanel',
        layout:'border',
        items:[{
            region:'center',
            xtype:'grid',
            margin:'5 5 5 5',
            store:'DeviceLinkStore',
            columns:[
                {xtype:'rownumberer',align:'center',width:40},
                {text:'设备名称',dataIndex:'device',flex:1.5, renderer:function(v){return v==null?'':v.name;}},
                {text:'事件名称',dataIndex:'event',flex:1},
                {text:'联动分区',dataIndex:'linkArea',flex:1.5, renderer:function(v){return v==null?'':v.name;}},
                {text:'联动设备',dataIndex:'linkDevice',flex:1.5, renderer:function(v){return v==null?'':v.name;}},
                {text:'联动事件',dataIndex:'linkEvent',flex:1}
            ],
            tbar:[{text:'增加',itemId:'linkAdd'},{text:'修改',itemId:'linkUpdate'},{text:'删除',itemId:'linkDel'}]
        },{
            region:'east',
            margin:'5 5 5 0',
            width:300,
            hidden:true
        }]
    },{
        title:'设备作业管理',
        xtype:'grid',
        margin:'5 5 5 5',
        itemId:'deviceWorkId',
        store:'DeviceWorkStore',
        selType:'checkboxmodel',
        columns:[
            {xtype:'rownumberer',align:'center',width:40},
            {text:'id',dataIndex:'workId',flex:1,hidden:true},
            {text:'设备',dataIndex:'device',flex:1.5,
                renderer:function(value){
                    return value.name;
                }},
            {text:'设备类型',dataIndex:'device',flex:1.5,
                renderer:function(value){
                    return value.typeName;
                }},
            {text:'周期',dataIndex:'period',flex:1,
                renderer:function(value){
                    if (value && value.toLowerCase() == 'day') {
                        return '每天'
                    }
                    else if (value && value.toLowerCase() == 'workday') {
                        return '工作日（周一到周五）'
                    }
                }
            },

            {text:'作业模式',dataIndex:'mode',flex:1,
                renderer:function(value) {
                    if (value && value.toLowerCase() == 'open') {
                        return '开启（布防）'
                    }
                    else {
                        return '关闭（撤防）'
                    }
                }
            },
            {text:'作业时间',dataIndex:'workTime',flex:1}
        ],
        tbar:[{text:'增加',itemId:'workAdd'},{text:'修改',itemId:'workUpdate'},{text:'删除',itemId:'workDel'}]
    },{
        title:'设备信息',
        xtype:'grid',
        margin:'5 5 5 5',
        itemId:'deviceInformation',
        store:'DeviceInformationStore',
        selType:'checkboxmodel',
        columns: [
            {xtype: 'rownumberer', align: 'center', width: 30},
            {text:'id',dataIndex:'inforid',flex:1,hidden:true},
            {text: '设备名', dataIndex: 'devicename', flex: 1},
            {text: '设备编号', dataIndex: 'devicecode', flex: 1},
            {text: '制造商', dataIndex: 'manufacturers', flex: 1},
            {text: '安装日期', dataIndex: 'installdate', flex: 1},
            {text: '厂家联系方式', dataIndex: 'pthone', flex: 1},
            {text: '管理人员', dataIndex: 'adminuser', flex: 1},
            {text: '保养周期', dataIndex: 'maintenance', flex: 1}
        ],
        tbar: ['关键字：', {
            xtype: 'combo',
            itemId:'searchcombo',
            width: 110,
            triggerAction: 'all',
            mode: 'local',
            typeAhead: true,
            anchor: '100%',
            store: new Ext.data.Store({
                fields: ['name', 'value'],
                data: [{
                    name: '设备名',
                    value: 'devicename'
                }, {
                    name: '设备编号',
                    value: 'devicecode'
                }]
            }),
            valueField: 'value',
            displayField: 'name'
        },{
            xtype: 'textfield',
            itemId:'content',
            style: 'width:300px'
        },{
            text:'查询',
            width:60,
            itemId:'selectBtn',
        },{
            text:'导出',
            width:60,
            itemId:'expBtn',
        },{
            text:'录入',
            width:60,
            itemId:'addBtn',
        },{
            text:'修改',
            width:60,
            itemId:'modifyBtn',
        },{
            text:'删除',
            width:60,
            itemId:'delBtn',
        }],
        bbar:{
            xtype: 'pagingtoolbar',
            displayInfo: true,
            displayMsg: '显示 {0} - {1} 条，共{2}条',
            emptyMsg: "没有数据显示",
            items:['-',{
                xtype:'combo',
                store: new Ext.data.ArrayStore({
                    fields: ['text', 'value'],
                    data: [['5', 5], ['10', 10], ['25', 25], ['50', 50], ['100', 100], ['300', 300],['1000', 1000]]
                }),
                displayField: 'text',
                value:pageSize,//使用默认分页大小
                valueField: '' +
                    '',
                editable: false,
                width: 80,
              }]
        }
    },{
        title:'设备故障诊断管理',
        xtype:'grid',
        margin:'5 5 5 5',
        itemId:'devicediagnose',
        store:'DeviceDiagnoseStore',
        selType:'checkboxmodel',
        columns: [
            {xtype: 'rownumberer', align: 'center', width: 30},
            {text:'id',dataIndex:'id',flex:1,hidden:true},
            {text: '故障名', dataIndex: 'diagnosename', flex: 1},
            {text: '故障编号', dataIndex: 'diagnosecode', flex: 1},
            {text: '故障描述', dataIndex: 'faultcause', flex: 1},
            {text: '建议', dataIndex: 'suggest', flex: 1},
            {text: '创建时间', dataIndex: 'createdate', flex: 1},
            {text: '修改时间', dataIndex: 'modifydate', flex: 1}
        ],
        tbar: [

        {
            text:'增加',
            width:60,
            itemId:'diagnoseAddBtn',
        },{
            text:'修改',
            width:60,
            itemId:'diagnoseModifyBtn',
        },{
            text:'删除',
            width:60,
            itemId:'diagnoseDelBtn',
        }],
        bbar:{
            xtype: 'pagingtoolbar',
            displayInfo: true,
            displayMsg: '显示 {0} - {1} 条，共{2}条',
            emptyMsg: "没有数据显示",
            items:['-',{
                xtype:'combo',
                store: new Ext.data.ArrayStore({
                    fields: ['text', 'value'],
                    data: [['5', 5], ['10', 10], ['25', 25], ['50', 50], ['100', 100], ['300', 300],['1000', 1000]]
                }),
                displayField: 'text',
                value:pageSize,//使用默认分页大小
                valueField: '' +
                    '',
                editable: false,
                width: 80,
            }]
        }
    },{
        title:'告警信息列表',
        xtype:'grid',
        margin:'5 5 5 5',
        itemId:'alarmId',
        store:'DeviceAlarmStore',
        selType:'checkboxmodel',
        columns: [
            {xtype: 'rownumberer', align: 'center', width: 30},
            {text:'id',dataIndex:'id',flex:1,hidden:true},
            {text: '告警类型', dataIndex: 'warningType', flex: 1},
            {text: '设备名称', dataIndex: 'device', flex: 1},
            {text: '警告描述', dataIndex: 'description', flex: 1},
            {text: '告警时间', dataIndex: 'warningTime', flex: 1},
            {text: '创建时间', dataIndex: 'createTime', flex: 1},
            {text: '状态', dataIndex: 'status', flex: 1}
        ],
        tbar: [
            {
                text:'确认告警状态',
                width:120,
                itemId:'alarmAffirmBtn',
            },  {
                text:'反确认告警状态',
                width:120,
                itemId:'alarmDenyBtn',
            }],
        bbar:{
            xtype: 'pagingtoolbar',
            displayInfo: true,
            displayMsg: '显示 {0} - {1} 条，共{2}条',
            emptyMsg: "没有数据显示",
            items:['-',{
                xtype:'combo',
                store: new Ext.data.ArrayStore({
                    fields: ['text', 'value'],
                    data: [['5', 5], ['10', 10], ['25', 25], ['50', 50], ['100', 100], ['300', 300],['1000', 1000]]
                }),
                displayField: 'text',
                value:pageSize,//使用默认分页大小
                valueField: '' +
                '',
                editable: false,
                width: 80,
            }]
        }
    },{
        title:'数据管理',
        xtype:'grid',
        margin:'5 5 5 5',
        itemId:'managementId',
        store:'ManagementHistoryStore',
        selType:'checkboxmodel',
        columns: [
            {xtype: 'rownumberer', align: 'center', width: 30},
            {text:'id',dataIndex:'id',flex:1,hidden:true},
            {text: '采集时间', dataIndex: 'time', flex: 1},
            {text: '数据描述', dataIndex: 'capturevalue', flex: 1},
            {text: '设备类型', dataIndex: 'type', flex: 1}
        ],
        tbar: ['关键字：', {
            xtype: 'combo',
            itemId:'searchcombo',
            width: 110,
            triggerAction: 'all',
            mode: 'local',
            typeAhead: true,
            anchor: '100%',
            store: new Ext.data.Store({
                fields: ['name', 'value'],
                data: [{
                    name: '采集时间',
                    value: 'captureTime'
                }, {
                    name: '数据描述',
                    value: 'captureValue'
                }, {
                    name: '设备类型',
                    value: 'type'
                }]
            }),
            valueField: 'value',
            displayField: 'name'
        },{
            xtype: 'textfield',
            itemId:'content',
            style: 'width:300px'
        },{
            text:'查询',
            width:60,
            itemId:'historySelectBtn',
        }, {
                text:'导出',
                width:100,
                itemId:'expHistoryBtn',
            }],
        bbar:{
            xtype: 'pagingtoolbar',
            displayInfo: true,
            displayMsg: '显示 {0} - {1} 条，共{2}条',
            emptyMsg: "没有数据显示",
            items:['-',{
                xtype:'combo',
                store: new Ext.data.ArrayStore({
                    fields: ['text', 'value'],
                    data: [['5', 5], ['10', 10], ['25', 25], ['50', 50], ['100', 100], ['300', 300],['1000', 1000]]
                }),
                displayField: 'text',
                value:pageSize,//使用默认分页大小
                valueField: '' +
                    '',
                editable: false,
                width: 80,
            }]
        }
    }]
});