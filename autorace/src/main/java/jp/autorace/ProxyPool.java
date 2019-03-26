package jp.autorace;

import java.util.Vector;

import org.apache.log4j.Logger;

import poseidon.bot.PageHelper;
import poseidon.config.Proxy;

public class ProxyPool
{
	static Logger logger = Logger.getLogger(ProxyPool.class.getName());
	private Vector _v = null;
	private int totalCount = 0;
	private int curProxyIndex = 0;
	public ProxyPool()
	{
		_v = new Vector();
	}
	
	public void LoadProxy()
	{
		PageHelper page = new PageHelper();
		logger.info("\r\n**************** start to load and test proxy ****************\r\n");
		StringBuffer sb = new StringBuffer("\r\n**************** proxy state list ****************\r\n");		
		Vector proxyv = App.config.getProxys();
		for(int i=0;i<proxyv.size();i++)
		{
			Proxy p = (Proxy)proxyv.get(i);
			String host = p.getHost();
			int port = Integer.parseInt(p.getPort());
			String user = p.getUser();
			String password = p.getPassword();		
			if(user.length()>0)
				page.setProxy(host,port,user,password);
			else
				page.setProxy(host, port);
			
			String proxystr = "proxy: "+host+":"+port+":"+user+":"+password;
//			logger.info("start to test proxy: "+proxystr);
//			if(page.PageOpen("http://autorace.jp/netstadium/Live/funabashi"))
//			{
//				sb.append(proxystr+" ::: OK\r\n");
//				_v.add(p);
//			}
//			else
//			{
//				sb.append(proxystr+" ::: NO\r\n");
//			}			
			_v.add(p);
			sb.append(proxystr+" ::: Loaded...\r\n");
		}
		logger.info(sb.toString());
	}
	
	public int size()
	{
		return _v.size();
	}
	
	public synchronized PageHelper GetProxyPage()
	{
		if(_v.size()>0)
		{
			Proxy p = (Proxy)_v.get(curProxyIndex);		
			PageHelper page = new PageHelper();
			String host = p.getHost();
			int port = Integer.parseInt(p.getPort());
			String user = p.getUser();
			String password = p.getPassword();		
			if(user.length()>0)
				page.setProxy(host,port,user,password);
			else
				page.setProxy(host, port);
			curProxyIndex++;
			if(curProxyIndex==_v.size())
				curProxyIndex = 0;
			return page;
		}
		return null;
	}
}
