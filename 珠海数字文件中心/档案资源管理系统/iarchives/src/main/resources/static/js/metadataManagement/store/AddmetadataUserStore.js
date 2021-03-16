/**
 * Created by SunK on 2020/8/22 0022.
 */
Ext.define('MetadataManagement.store.AddmetadataUserStore',{
    extend:'Ext.data.Store',
    autoLoad: false,
    fields: ['userid','realname'],
    proxy: {
        type: 'ajax',
        url: '/serviceMetadata/getuserList',
        extraParams: {
            userid:''
        },
        reader: {
            type: 'json'
        }
    }
});