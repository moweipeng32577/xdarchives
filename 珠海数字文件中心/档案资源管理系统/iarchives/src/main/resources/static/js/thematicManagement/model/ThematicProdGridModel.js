/**
 * Created by yl on 2017/11/1.
 */
Ext.define('ThematicProd.model.ThematicProdGridModel',{
    extend:'Ext.data.Model',
    fields: [
        {name: 'id', type: 'string', mapping: 'thematicid'},
        {name: 'title', type: 'string'},
        {name: 'thematiccontent', type: 'string'},
        {name: 'publishstate', type: 'string'},
        {name: 'backgroundpath', type: 'string'}
    ]
});