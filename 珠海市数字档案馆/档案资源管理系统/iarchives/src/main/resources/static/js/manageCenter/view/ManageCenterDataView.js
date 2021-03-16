/**
 * Created by Administrator on 2020/7/21.
 */


Ext.define('ManageCenter.view.ManageCenterDataView', {
    extend: 'Comps.view.BasicDataView',
    xtype: 'manageCenterDataView',
    hasPageBar:true,            //分页栏
    hasSearchBar:false,          //搜索栏
    hasCloseButton:false,        //关闭按钮
    hasCancelButton:false,
    tbar: [{
        text:'查看',
        itemId:'lookId',
        margin: '0 0 0 10'
    }],
    datastore: 'ManageCenterDataStore'
});
