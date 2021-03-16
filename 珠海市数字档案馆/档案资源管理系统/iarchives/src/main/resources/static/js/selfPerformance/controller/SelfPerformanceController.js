/**
 * Created by Administrator on 2020/4/13.
 */

Ext.define('SelfPerformance.controller.SelfPerformanceController', {
    extend: 'Ext.app.Controller',
    views: ['SelfPerformanceGridView'],
    stores: ['SelfPerformanceGridStore'],
    models: ['SelfPerformanceGridModel'],
    init: function () {
        this.control({
            'selfPerformanceGridView':{
                afterrender:function (view) {
                    view.initGrid();
                }
            }
        })
    }
});
