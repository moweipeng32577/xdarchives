/**
 * Created by tanly on 2017/11/8 0024.
 */
Ext.define('MetadataTemplate.store.MetadataTemplateTreeStore',{
    extend:'Ext.data.TreeStore',
    model:'MetadataTemplate.model.MetadataTemplateTreeModel',
    proxy: {
        type: 'ajax',
        url: '/metadataTemplate/getClassifyById',
        extraParams:{pcid:''},
        reader: {
            type: 'json',
            expanded: true
        }
    },
    root: {
        text: '数据分类',
        expanded: true

    },
    // listeners:{
    //     nodebeforeexpand:function(node, deep, animal) {
    //         // if((node.raw)){
    //             this.proxy.extraParams.pcid = node.get('fnid');
    //             this.proxy.url = '/metadataTemplate/getClassifyById';
    //         // };
    //     }
    // }
});