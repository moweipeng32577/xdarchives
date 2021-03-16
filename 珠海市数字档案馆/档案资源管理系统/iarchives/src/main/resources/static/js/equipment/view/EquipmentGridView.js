Ext.define('Equipment.view.EquipmentGridView',{
    extend: 'Comps.view.BasicGridView',
    xtype:'equipmentGridView',
    itemId:'equipmentGridViewID',
    bodyBorder: false,
    store: 'EquipmentGridStore',
    hasCloseButton:false,
    head:false,
    searchstore:[
        {item: "name", name: "设备名称"},
        {item: "type", name: "设备类型"},
        {item: "purchasetime", name: "采购时间"}
    ],
    tbar: functionButton,
    columns: [
        {text: '设备名称', dataIndex: 'name', flex: 2, menuDisabled: true},
        // {text: '内容', dataIndex: 'text', flex: 4, menuDisabled: true,renderer: function(value, cellmeta, record) {
        //         var reTag = /<(?:.|\s)*?>/g;
        //         return value.replace(reTag,"");
        //     } },
        // {text: '创建人', dataIndex: 'postedman', flex: 1, menuDisabled: true},
        {text: '设备类型', dataIndex: 'type', flex: 1.5, menuDisabled: true},
        {text: '品牌', dataIndex: 'brand', flex: 1.5, menuDisabled: true},
        {text: '型号', dataIndex: 'model', flex: 1, menuDisabled: true},
        {text: '规格', dataIndex: 'specifications', flex: 1, menuDisabled: true},
        // {text: '发布状态', dataIndex: 'publishstate', flex: 1, menuDisabled: true,
        //     renderer:function (value) {
        //         if (value == '1'){
        //             return '已发布';
        //         }else {
        //             return '未发布';
        //         }
        //     }},
        {text: '单价', dataIndex: 'price', flex: 1, menuDisabled: true},
        {text: '数量', dataIndex: 'amount', flex: 1, menuDisabled: true},
        {text: '备案时间', dataIndex: 'purchasetime', flex: 1, menuDisabled: true},
        {text: '到货验收时间', dataIndex: 'acceptancetime', flex: 1, menuDisabled: true},
        {text: '所属部门', dataIndex: 'organname', flex: 1, menuDisabled: true},
        {text: 'ip地址', dataIndex: 'ipaddress', flex: 1, menuDisabled: true},
        {text: '备注', dataIndex: 'remarks', flex: 1, menuDisabled: true}
        // {text: '附件', dataIndex: 'enclosure', flex: 1, menuDisabled: true}
    ]
});