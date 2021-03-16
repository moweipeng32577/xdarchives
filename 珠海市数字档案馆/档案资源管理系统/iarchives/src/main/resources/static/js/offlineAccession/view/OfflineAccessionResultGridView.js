/**
 * Created by yl on 2019/1/10.
 */
Ext.define('OfflineAccession.view.OfflineAccessionResultGridView', {
    extend: 'Comps.view.BasicGridView',
    xtype: 'offlineAccessionResultGridView',
    tbar: [
        {
            xtype: 'button',
            itemId:'lookdetails',
            text: '检测详情'
        }, '-', {
            xtype: 'button',
            itemId:'lookPackpage',
            text: '查看数据包'
        }, '-', {
            xtype: 'button',
            itemId:'insert',
            text: '接入系统'
        }],
    hasSearchBar:false,
    store: 'OfflineAccessionResultGridStore',
    columns: [
        {xtype: "rownumberer", text: "序号", width: 80, align: 'center'},
        {name: 'docid', hidden: true},
        {text: '文件名称', dataIndex: 'filename', flex: 3, menuDisabled: true},
        {text: '检测状态', dataIndex: 'checkstatus', flex: 1, menuDisabled: true},
        {text: '准确性', dataIndex: 'authenticity', flex: 1, menuDisabled: true,renderer: function(value, cellmeta, record) {
            if(value.indexOf('验证不通过') > 0){
                return "<span style=\"color:red\">验证不通过</span>"
            }else if(value.indexOf('验证通过') > 0){
                return "<span style=\"color:green\">验证通过</span>"
            }else{
                return  value;
            }
        }},
        {text: '完整性', dataIndex: 'integrity', flex: 1, menuDisabled: true,renderer: function(value, cellmeta, record) {
            if(value.indexOf('验证不通过') > 0){
                return "<span style=\"color:red\">验证不通过</span>"
            }else if(value.indexOf('验证通过') > 0){
                return "<span style=\"color:green\">验证通过</span>"
            }else{
                return  value;
            }
        }},
        {text: '可用性', dataIndex: 'usability', flex: 1, menuDisabled: true,renderer: function(value, cellmeta, record) {
            if(value.indexOf('验证不通过') > 0){
                return "<span style=\"color:red\">验证不通过</span>"
            }else if(value.indexOf('验证通过') > 0){
                return "<span style=\"color:green\">验证通过</span>"
            }else{
                return  value;
            }
        }},
        {text: '安全性', dataIndex: 'safety', flex: 1, menuDisabled: true,renderer: function(value, cellmeta, record) {
            if(value.indexOf('验证不通过') > 0){
                return "<span style=\"color:red\">验证不通过</span>"
            }else if(value.indexOf('验证通过') > 0){
                return "<span style=\"color:green\">验证通过</span>"
            }else{
                return  value;
            }
        }},
        {text: '是否接入', dataIndex: 'isaccess', flex: 1, menuDisabled: true}
    ]
});
