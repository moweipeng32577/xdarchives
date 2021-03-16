
Ext.Loader.setConfig({
    disableCaching : false
});
Ext.application({
    requires : [ 'Ext.container.Viewport' ],

    name : 'Reservation', // 定义的命名空间
    appFolder : '../js/reservation', // 指明应用的根目录

    controllers : ['ReservationAdminsController'],

    launch : function() {
        Ext.create('Ext.container.Viewport', {
            layout : 'fit',
            items : {
                xtype : 'reservationView'
            }
        });
    }
});