Ext.define('Acquisition.view.OAImportGridView', {
    extend: 'Comps.view.BasicGridView',
    hasSearchBar:false,
    xtype: 'oAImportGridView',
    itemId:'oAImportGridViewID',
    region: 'center',
    allowDrag:true,
    searchstore: [
        {item: "title", name: "标题"},
        {item: "filename", name: "OA包名"},
        {item: "date", name: "接收日期"},
        {item: "filesize", name: "文件大小"},
        {item: "filestate", name: "文件状态"},
        {item: "receivestate", name: "接收状态"}
    ],
    tbar:[/*{
        itemId:'ReceiveOA',
        xtype: 'button',
        text: '接收OA'
    }, '-', {
        itemId:'downloadOA',
        xtype: 'button',
        text: '下载数据包'
    }, '-', {
        itemId:'downloadImportMsg',
        xtype: 'button',
        text: '导出OA接收信息'
    }, '-',*/ {
        text: '查看验证明细',
        itemId: 'lookdetail',
        iconCls: 'fa fa-eye'
    }, '-',{
        text: '查看数据包',
        itemId: 'lookPackpage',
        iconCls: 'fa fa-eye'
    }, '-',{
        itemId:'back',
        xtype: 'button',
        text: '返回'
    }],
    store: 'OAImportGridStore',
    columns: [
        {text: '标题', dataIndex: 'title', flex: 2, menuDisabled: true},
        {text: 'OA包名', dataIndex: 'filename', flex: 2, menuDisabled: true},
        {text: '接收日期', dataIndex: 'date', flex: 2, menuDisabled: true},
        {text: '文件大小', dataIndex: 'filesize', flex: 2, menuDisabled: true},
        {text: '文件状态', dataIndex: 'filestate', flex: 2, menuDisabled: true},
        {text: '接收状态', dataIndex: 'receivestate', flex: 2, menuDisabled: true},
        {
            text: '准确性',
            dataIndex: 'authenticity',
            flex: 1,
            menuDisabled: true,
            renderer: function (value, cellmeta, record) {
                if (typeof (value) == 'undefined' || value == '') {
                    return '未检测';
                } else if (value.indexOf('验证不通过') != -1) {
                    return "<span style=\"color:red\">验证不通过</span>"
                } else if (value.indexOf('验证通过') != -1) {
                    return "<span style=\"color:green\">验证通过</span>"
                } else {
                    return value;
                }
            }
        },
        {
            text: '完整性',
            dataIndex: 'integrity',
            flex: 1,
            menuDisabled: true,
            renderer: function (value, cellmeta, record) {
                if (typeof (value) == 'undefined' || value == '') {
                    return '未检测';
                } else if (value.indexOf('验证不通过') != -1) {
                    return "<span style=\"color:red\">验证不通过</span>"
                } else if (value.indexOf('验证通过') != -1) {
                    return "<span style=\"color:green\">验证通过</span>"
                } else {
                    return value;
                }
            }
        },
        {
            text: '可用性',
            dataIndex: 'usability',
            flex: 1,
            menuDisabled: true,
            renderer: function (value, cellmeta, record) {
                if (typeof (value) == 'undefined' || value == '') {
                    return '未检测';
                } else if (value.indexOf('验证不通过') != -1) {
                    return "<span style=\"color:red\">验证不通过</span>"
                } else if (value.indexOf('验证通过') != -1) {
                    return "<span style=\"color:green\">验证通过</span>"
                } else {
                    return value;
                }
            }
        },
        {
            text: '安全性',
            dataIndex: 'safety',
            flex: 1,
            menuDisabled: true,
            renderer: function (value, cellmeta, record) {
                if (typeof (value) == 'undefined' || value == '') {
                    return '未检测';
                } else if (value.indexOf('验证不通过') != -1) {
                    return "<span style=\"color:red\">验证不通过</span>"
                } else if (value.indexOf('验证通过') != -1) {
                    return "<span style=\"color:green\">验证通过</span>"
                } else {
                    return value;
                }
            }
        },
        {
            text: '封装状态',
            dataIndex: 'base',
            flex: 1,
            menuDisabled: true,
            renderer: function (value, cellmeta, record) {
                var checkstatus = record.get('checkstatus');
                if (typeof (checkstatus) == 'undefined' || checkstatus == '' || checkstatus.indexOf('不通过') != -1) {
                    return '未封装';
                }
                else if (checkstatus.indexOf('通过') != -1) {
                    return "<span style=\"color:green\">已封装</span>"
                }
                else {
                    return checkstatus;
                }
            }
        }
    ],
    hasSelectAllBox:true
});
