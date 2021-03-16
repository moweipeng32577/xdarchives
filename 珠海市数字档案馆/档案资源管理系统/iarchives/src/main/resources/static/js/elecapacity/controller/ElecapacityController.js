/**
 * Created by xd on 2017/10/21.
 */
Ext.define('Elecapacity.controller.ElecapacityController', {
    extend: 'Ext.app.Controller',

    views: ['ElecapacityView','ElecapacityTabView','ElecapacityDetailView','ElecapacityListView'],//加载view
    stores: ['ElecapacityListStore'],//加载store
    models: ['ElecapacityListModel'],//加载model
    init: function (view) {
        this.control({
            'elecapacityDetailView': {
                render: function (view) {
                    var form = view.down('form');
                    form.load({
                        url: '/elecapacity/getcapacity',
                        method: 'GET',
                        success: function () {
                        },
                        failure: function () {
                            XD.msg('获取表单信息失败');
                        }
                    });
                }
            },
            elecapacityListView:{
                render:function (view) {
                    var store =  view.getStore();
                    store.reload();
                }
            }
        })
    }
})