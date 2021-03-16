/**
 * Created by Rong on 2018/4/27.
 */
Ext.define('Moveware.model.DetailGridModel',{
    extend:'Ext.data.Model',
    fields: [
        {name: 'id', type: 'string',mapping:'shid'},
        {name: 'coldisplay', type: 'string'},
        {name: 'sectiondisplay', type: 'string'},
        {name: 'layerdisplay', type: 'string'},
        {name: 'sidedisplay', type: 'string'}
    ]
});