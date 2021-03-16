/**
 * Created by RonJiang on 2018/4/20 0020.
 */
Ext.define('LongRetention.view.LongRetentionGridView', {
    extend: 'Comps.view.BasicGridView',
    xtype: 'longRetentionGridView',
    title: '长期保管',
    searchstore: [
        {item: 'title', name: '题名'},
        {item: 'archivecode', name: '档号'}
    ],
    tbar: [{
        xtype: 'button',
        text: '查看原文',
        itemId: 'look',
        iconCls: 'fa fa-eye'
    }, '-', {
        text: '查看封装包',
        itemId: 'lookpacket',
        iconCls: 'fa fa-eye'
    }, '-', {
        text: '执行验证',
        itemId: 'implement',
        iconCls: 'fa fa-tripadvisor'
    }, '-', {
        text: '查看验证明细',
        itemId: 'lookdetail',
        iconCls: 'fa fa-eye'
    }, '-',{
        text: '状态重置',
        itemId: 'reset',
        iconCls: 'fa fa-reply-all'
    }, '-',{
        text: '下载封装包',
        itemId: 'download',
        iconCls: 'fa fa-download'
    }, '-',{
        text: '定时任务设置',
        itemId: 'set',
        iconCls: 'fa fa-list-ul'
    }, '-',{
        text: '验证项设置',
        itemId: 'validationset',
        iconCls: 'fa fa-list-ul'
    }],
    store: 'LongRetentionGridStore',
    columns: [
        {text: '题名', dataIndex: 'title', flex: 2, menuDisabled: true},
        {text: '档号', dataIndex: 'archivecode', flex: 2, menuDisabled: true},
        {text: '检测状态', dataIndex: 'checkstatus', flex: 1, menuDisabled: true},
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