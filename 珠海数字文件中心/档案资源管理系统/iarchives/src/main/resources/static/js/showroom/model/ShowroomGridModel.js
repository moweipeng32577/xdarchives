/**
 * Created by zdw on 2020/03/20
 */
Ext.define('Showroom.model.ShowroomGridModel',{
    extend:'Ext.data.Model',
    fields: [
        {name: 'id', type: 'string',mapping:'showroomid'},
        {name: 'title', type: 'string'},
        {name: 'content', type: 'string'},
        {name: 'appendix', type: 'string'},
        {name: 'flag', type: 'string'},
        {name: 'audiences', type: 'int'},
        {name: 'sequence', type: 'int'}
    ]
});