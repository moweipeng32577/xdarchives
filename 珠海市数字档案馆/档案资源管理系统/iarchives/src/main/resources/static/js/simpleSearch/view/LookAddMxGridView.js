/**
 * Created by yl on 2017/11/3.
 */
Ext.define('SimpleSearch.view.LookAddMxGridView', {
    extend: 'Comps.view.BasicGridView',
    xtype: 'lookAddMxGridView',
    itemId: 'lookAddMxGridViewId',
    region: 'center',
    hasSearchBar: false,
    tbar: [{
        itemId: 'stAddSq',
        xtype: 'button',
        text: '查档申请'
    },{
        itemId:'remove',
        xtype:'button',
        text:'移除'
    },{
        itemId:'setPrint',
        xtype:'button',
        text:'设置打印范围',
        hidden:true
    }, {
        itemId: 'close',
        xtype: 'button',
        text: '返回'
    }],
    store: 'LookAddMxGridStore',
    listeners:{//排序监听
        headerclick: function (ct, c, e) {
            var store = c.up('grid').getStore();
            if(c.dataIndex!=='archivecode'&&store.totalCount > 100000&&c.dataIndex!=='nodefullname'){//档号是索引字段，可排序
                /*e.stopPropagation();*/
                XD.msg('查询数据量大于10万,只支持本页面排序');
                store.setRemoteSort(false);
            }else if(c.dataIndex==='nodefullname'){//所属节点不排序
                XD.msg('所属分类不支持排序');
                store.setRemoteSort(false);
            }else{
                store.setRemoteSort(true);
            }
        }
    },
    columns: [
        {
            xtype:'actioncolumn',
            resizable:false,//不可拉伸
            hideable:false,
            header: '原文',
            dataIndex: 'eleid',
            sortable:true,
            width:60,
            align:'center',
            items:['@file']
        },
        {text: '题名', dataIndex: 'title', flex: 2, menuDisabled: true},
        {text: '文件编号', dataIndex: 'filenumber', flex: 2, menuDisabled: true},
        {text: '档号', dataIndex: 'archivecode', flex: 2, menuDisabled: true},
        {text: '全宗号', dataIndex: 'funds', flex: 2, menuDisabled: true},
        {text: '目录号', dataIndex: 'catalog', flex: 2, menuDisabled: true},
        {text: '所属分类', dataIndex: 'nodefullname', flex: 2, menuDisabled: true}
    ]
});