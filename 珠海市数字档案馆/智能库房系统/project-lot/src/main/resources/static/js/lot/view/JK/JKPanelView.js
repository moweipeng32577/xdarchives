/**
 * Created by Rong on 2019-01-17.
 */
Ext.define('Lot.view.JK.JKPanelView',{
    extend:'Ext.panel.Panel',
    xtype:'JKPanel',
    top: 50,
    left:100,
    listeners:{
        render:function(jkpanel){
            if(jkpanel.device){
                var prop = Ext.decode(jkpanel.device.get('prop'));

                if(prop.ip.indexOf("10.10.100") != -1){
                    var url = '/oldjk?path=' + 'http://10.10.100.3:8012/chznkg-ibuilding-webapp/ocx.jsp' +
                        '&addr='+prop.ip+'&port='+prop.port+'&user='+prop.user+'&pwd='+prop.pwd+'&channel='+prop.channel
                }
                else{
                    var url = '/jk?addr=' + prop.ip + '&port=' + prop.port + '&user=' + prop.user
                        + '&pwd=' + prop.pwd + '&channel=' + prop.channel;

                }
                var tpl = new Ext.XTemplate('<iframe class="frame" src={url} frameborder=0 scrolling=no style="width:100%;height:100%;border:0;marginWidth:0;marginHeight:0;"></iframe>');
                tpl.compile();
                tpl.overwrite(this.body, {
                    url : url
                });
            }
        }
    }
});