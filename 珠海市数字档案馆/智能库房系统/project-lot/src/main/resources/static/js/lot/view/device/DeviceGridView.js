Ext.define('Lot.view.device.DeviceGridView',{
    extend:'Ext.grid.Panel',
    xtype:'DeviceGridView',
    store:'DevicePanelStore',
    tbar:[{text:'增加',itemId:'deviceAddBtn'},{text:'修改',itemId:'devicemodifyBtn'},{text:'删除',itemId:'devicedelBtn'},
        /* {text:'效果预览'},{text:'导入设备数据'},{text:'导入设备数据'},{text:'禁用设备',itemId:'DisableDevice'},{text:'启用设备',itemId:'EnabledDevice'}*/],
    selType:'checkboxmodel',
    columns:[
        {xtype: 'rownumberer', align: 'center', width:40},
        {text: '名称', dataIndex: 'name', flex: 2},
        {text: '类型', dataIndex: 'typeName', flex: 1},
        {
            text: '设备分区', dataIndex: 'area', flex: 1,
            renderer: function (value) {
                return value == null ? "" : value.name;
            }
        }, {
            text: '启用状态', dataIndex: 'enabled', width: 160,
            renderer: function (value) {
                if (value != null && value == "0") {
                    return "启用";
                }
                else {
                    return "禁用";
                }
            }
        }, {
            xtype: 'gridcolumn',
            width:160,
            dataIndex: 'operate',
            text: '是否接入',
            align: 'center',
            itemId:'jionbtn',
            renderer: function (value, metaData, record) {
                var str;
                if(record.data.enabled!='0'){
                    str = "<input type='button' value='接入'>";
                }else{
                    str = "";
                }
                return str;
            }
        }
    ]
});