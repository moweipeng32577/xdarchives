/**
 * Created by Administrator on 2018/11/8.
 */

Ext.define('Thematicelectronic.model.ThematicDetailGridModel',{
    extend:'Ext.data.Model',
    fields: [
        {name: 'id', type: 'string', mapping: 'thematicdetilid'},
        {name: 'thematicid', type: 'string'},
        {name: 'title', type: 'string'},
        {name: 'filedate', type: 'string'},
        {name: 'responsibleperson', type: 'string'},
        {name: 'filecode', type: 'string'},
        {name: 'subheadings', type: 'string'},
        {name: 'mediatext', type: 'string'}
    ]
});
