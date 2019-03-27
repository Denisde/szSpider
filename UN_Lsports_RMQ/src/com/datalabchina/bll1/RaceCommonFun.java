/*
 * Created on 2004-12-9
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.datalabchina.bll1;

import java.io.File;
import java.math.BigDecimal;
import java.sql.ResultSet;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;
import java.util.regex.Matcher;

//import jregex.Replacer;




import org.apache.log4j.Logger;

import com.common.CommonFun;
import com.common.db.ZTStd;

/**
 * @author Administrator
 * 
 * TODO To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Style - Code Templates
 */
public class RaceCommonFun {

	public static Logger logger = Logger.getLogger(RaceCommonFun.class.getName());

	public static String moneyPatter = "&pound;(\\d{1,4}\\.\\d{2})";//TRICAST &pound;8775.33

	public static boolean isPrePage(String content)
	{
		if(content.indexOf("Comments In Running")==-1)
			return true;
		return false;
	}

	public static boolean isPostPage(String content)
	{
		if(content.indexOf("Comments In Running")!=-1)
			return true;
		return false;
	}
	
	public static String GetDistanceStr(String strHtml){
		//		<div class="">
//      <div class="meetingDescription">Richmondshire Conditional Jockeys&acute; Handicap Hurdle</div>
//      <div class="meetingDescriptionShort gray ">
//         (Class 4)      (0-110, 3yo+)      <span class="gray">
//        2m3f      Good      </span>
//  <span class="black">10 hdles</span>
//        <br />
//  &pound;3,252.50, &pound;955.00, &pound;477.50, &pound;238.50             </div>
//   </div>
		
//		<div class="">
//      <div class="meetingDescription">Lindley Catering Selling Hurdle</div>
//      <div class="meetingDescriptionShort gray ">
//         (Class 5)      (4yo+)      <span class="gray">
//        3m2f      Good To Soft      </span>
//  <span class="black">13 hdles</span>
//        <br />
//  &pound;1,951.50, &pound;573.00, &pound;286.50, &pound;143.10             </div>
//   </div> 
		
//		<div class="meetingDescriptionShort gray ">
//        (Class 4)      (4yo+)      <span class="gray">
//       2m3f      Good      </span>
		
//		<div class="meetingDescriptionShort gray ">
//        (Class 4)      (0-110, 4yo+)      <span class="gray">
// (3m110y)      3m&frac12;f      Good To Soft      </span>
		
		
//		<div class="leftColBig">
//		 <h1>Towcester Result<br />07 Oct 2009</h1>
//			<h3 class="clearfix">
//		 <span class="timeNavigation">
//		 2:30 <a href="/horses/result_home.sd?race_id=490985&amp;r_date=2009-10-07&amp;popup=yes" class="bull"><b>&raquo;</b></a> </span> 
//		 gg.com Selling Hurdle	</h3>
//
//		 <ul>
//		 <li>
//		 (Class 5) (4-7yo) (2m3f110y) 2m3&frac12;f Good To Firm 10 hdles </li>
//		 <li>&pound;1,951.50, &pound;573.00, &pound;286.50, &pound;143.10</li>
//		 </ul>
//		 </div>
		
//		<div class="leftColBig">
//		 <h1>Kempton (AW) Result<br />07 Oct 2009</h1>
//			<h3 class="clearfix">
//		 <span class="timeNavigation">
//		 <a href="/horses/result_home.sd?race_id=490900&amp;r_date=2009-10-07&amp;popup=yes" class="bull"><b>&laquo;</b></a>
//		 9:20 </span> 
//		 kempton.co.uk Handicap	</h3>
//
//		 <ul>
//		 <li>
//		 (Class 6) (0-60, 3yo+) 1m Standard </li>
//		 <li>&pound;2,047.20, &pound;604.50, &pound;302.40</li>
//		 </ul>
//		 </div>
		
//		<div class="leftColBig">
//		 <h1>Worcester Result<br />08 Oct 2009</h1>
//			<h3 class="clearfix">
//		 <span class="timeNavigation">
//		 2:00 </span> 
//		 Ladbrokes At Worcester Racecourse Conditional Jockeys' Handicap Chase	</h3>
//
//		 <ul>
//		 <li>
//		 (Class 4) (0-100, 4yo+) 2m Good To Soft 12 fences </li>
//		 <li>&pound;4,228.25, &pound;1,241.50, &pound;620.75, &pound;310.05</li>
//		 </ul>
		
//		<p><span class="uppercase"><strong>Sponsor A Race At Tramore Handicap</strong></span>(3yo 47-65) Winner &euro;4,837<strong> ATR </strong><br />
//		<strong>Good </strong><strong>1m4f</strong>
//		Number of runners: 13</p>
		
//		<div class="leftColBig">
//		 <h1>Hereford Result<br />10 Sep 2003</h1>
//			<h3 class="clearfix">
//		 <span class="timeNavigation">
//		 <a href="/horses/result_home.sd?race_id=338228&amp;r_date=2003-09-10&amp;popup=yes" class="bull"><b>&laquo;</b></a>
//		 3:50 <a href="/horses/result_home.sd?race_id=338230&amp;r_date=2003-09-10&amp;popup=yes" class="bull"><b>&raquo;</b></a>
//		 </span> 
//		 casinotimes.co.uk "Play Online - Win A Fortune!" Junior Selling Hurdle (Class G)	</h3>
//
//		 <ul>
//		 <li>
//		 (Class 5) (3-4yo) 2m1f Good To Firm 8 hdles </li>
//		 <li>&pound;1,862.00, &pound;532.00, &pound;266.00</li>
//		 </ul>
//		 </div>		
		
//		<div class="leftColBig">
//		 <h1>Leicester Result<br />26 Jan 2010</h1>
//			<h3 class="clearfix">
//		 <span class="timeNavigation">
//		 <a href="/horses/result_home.sd?race_id=497123&amp;r_date=2010-01-26&amp;popup=yes" class="bull"><b>&laquo;</b></a>
//		 2:40 <a href="/horses/result_home.sd?race_id=497125&amp;r_date=2010-01-26&amp;popup=yes" class="bull"><b>&raquo;</b></a>
//		 </span> 
//		 European Breeders' Fund Mares' "National Hunt" Novices' Hurdle (Qualifier)	</h3>
//
//		 <ul>
//		 <li>
//		 (Class 3) (4yo+) 2m Heavy 8 hdles </li>
//		 <li>&pound;5,069.60, &pound;1,497.60, &pound;748.80, &pound;374.40, &pound;187.20</li>
//		 </ul>
//		 </div>
		
//		<div class="leftColBig">
//		 <h1>Leicester Result<br />26 Jan 2010</h1>
//			<h3 class="clearfix">
//		 <span class="timeNavigation">
//		 1:40 <a href="/horses/result_home.sd?race_id=497123&amp;r_date=2010-01-26&amp;popup=yes" class="bull"><b>&raquo;</b></a>
//		 </span> 
//		 Croxton Park Novices' Hurdle	</h3>
//
//		 <ul>
//		 <li>
//		 (Class 4) (4yo+) (2m4f110y) 2m4&frac12;f Heavy 10 hdles </li>
//		 <li>&pound;4,553.50, &pound;1,337.00, &pound;668.50, &pound;333.90</li>
//		 </ul>
//		 </div>		 
		 
//		logger.info(strHtml);
		
//		 <ul>
//		 <li>
//		 (Class 5) 
//		 (0-75, 3yo+) (7f50y)
//		 
//		 7f Good To Soft </li>
//		 <li>&pound;4,204.85, &pound;1,251.25, &pound;625.30, &pound;312.65</li>
//		 </ul>
		
//		<ul>
//		 <li>
//		 (Class 6) 
//		 (0-60, 3yo+) (1m3f101y)
//		 
//		 1m3&frac12;f Soft </li>
//		 <li>&pound;1,617.25, &pound;481.25, &pound;240.50, &pound;120.25</li>
//		 </ul>
		
//		 <ul>
//		 <li>
//		 (Class 2) 
//		 (0-100, 4yo+) 
//		 1m Good To Soft </li>
//		 <li>&pound;14,754.00, &pound;5,166.00, &pound;2,361.00, &pound;1,842.00, &pound;1,623.00, &pound;1,182.00, &pound;960.00, &pound;738.00, &pound;516.00, &pound;369.00</li>
//		 </ul>
		 

//		 <ul>
//		 <li>
//		 (Class 6) 
//		 (0-85, 3yo+) 
//		 1m2f Good To Firm </li>
//		 <li></li>
//		 </ul>
		 
		String pattern = "\\(((\\dm)?\\d{1,3}f\\d{1,5}y)\\)";
		Matcher matcher = CommonFun.GetMatcherStrGroup(strHtml, pattern);
		if (matcher.find()) {
			String distance = matcher.group(1).trim();
			if (distance.length()>0)
				return distance;
		}	
		
		pattern = "\\(((\\dm)?\\d{1,5}y)\\)";
		matcher = CommonFun.GetMatcherStrGroup(strHtml, pattern);
		if (matcher.find()) {
			String distance = matcher.group(1).trim();
			if (distance.length()>0)
				return distance;
		}
		
		pattern = "<ul>\\s+<li>\\s+(\\(.+?\\)\\s+){1,3}(.+?)</li>\\s+<li>((&pound;[,0-9.]{3,15},? ?){1,15})</li>\\s+</ul>";
		matcher = CommonFun.GetMatcherStrGroup(strHtml, pattern);
		if (matcher.find()) {
			String distance = matcher.group(2).trim().split(" ")[0];
			if (distance.length()>0)
				return distance;
		}
		
		pattern = "<strong>([a-zA-Z0-9]{1,50})</strong>\\s+Number of runners";
		matcher = CommonFun.GetMatcherStrGroup(strHtml, pattern);
		if (matcher.find()) {
			String distance = matcher.group(1).split(" ")[0];
			if (distance.length()>0)
				return distance;
		}
		
		pattern = "<div class=\"leftColBig\">\\s+<h1>.*?</h1>\\s+<h3 class=\"clearfix\">.*?</h3>\\s+<ul>\\s+<li>\\s+\\(.*?\\)( \\(.*?\\))? \\(?([0-9a-z]{2,50})\\)?( [0-9a-z&;]{2,50})?( [a-zA-Z ]{3,50})(\\d{1,2} [a-z]{3,50} )?(\\d{1,2} [a-z]{3,50})? </li>";
		matcher = CommonFun.GetMatcherStrGroup(strHtml, pattern, true);
		if (matcher.find()) {
			String distance = matcher.group(2).trim();
			if (distance.length()>0)
				return distance;
		}
		
		logger.debug("distanceraw============="+strHtml);
		String patter = "<div class=\"meetingDescriptionShort gray \">.*?<span class=\"gray\">(.*?)</span>";
		matcher = CommonFun.GetMatcherStrGroup(strHtml,patter);
		if(matcher.find())
		{
			String temp = matcher.group(1).trim();
			logger.debug("GetDistanceStrGetDistanceStrGetDistanceStr="+temp);
			patter = "\\((.*?)\\)";
			matcher = CommonFun.GetMatcherStrGroup(temp,patter);
			if(matcher.find())
				return matcher.group(1);
			else
				return temp.split(" ")[0];
		}
		
//		 <ul>
//		 <li>
//		 (4yo+) 1m </li>
//		 <li>&pound;6,465.52, &pound;2,586.21, &pound;1,939.66, &pound;1,293.10, &pound;646.55</li>
//		 </ul>

		pattern = "<ul>\\s+<li>\\s+(.+?)</li>\\s+<li>((&pound;[,0-9.]{3,15},? ?){1,9})</li>\\s+</ul>";
		matcher = CommonFun.GetMatcherStrGroup(strHtml, pattern);
		if (matcher.find()) {
			String distance = matcher.group(1).trim().split(" ")[1];
			if (distance.length()>0)
				return distance;
		}
		
		pattern = " (\\dm\\d{1,3}f) ";
		matcher = CommonFun.GetMatcherStrGroup(strHtml, pattern);
		if (matcher.find()) {
			String distance = matcher.group(1).trim();
			if (distance.length()>0)
				return distance;
		}
		
//		 <ul>
//		 <li>
//		 
//		 (4yo+) 
//		 2m Heavy </li>
//		 <li></li>
//		 </ul>
		 
		pattern = "<ul>\\s+<li>\\s+(.*?)</li>\\s+<li></li>\\s+</ul>";
		matcher = CommonFun.GetMatcherStrGroup(strHtml, pattern);
		if (matcher.find()) {
			String distance = CommonFun.GetStrFromPatter(matcher.group(1).trim()," (\\dm) ",1);
			if (distance.length()>0)
				return distance;
		}
		 
		return null;
	}	

	public static HashMap getComments(String content)
	{

		HashMap hp=new HashMap();
//		<table ALIGN=CENTER WIDTH=100% cellpadding=0 cellspacing=0 class="B2">
//		<tr height=14 bgcolor="#f5f5f5" onMouseOut="this.bgColor='#f5f5f5'" onMouseOver="this.bgColor='#66B821'">
//		<td style='text-align:justify'>
//		<b>Princess Taise</b></a><BR>

//		made virtually all, hung left from over 2f out, ran on</td></tr>
//		<tr height=3 bgcolor="#FFFFFF"><td> </td></tr>
//		<tr height=14 bgcolor="#f5f5f5" onMouseOut="this.bgColor='#f5f5f5'" onMouseOver="this.bgColor='#66B821'">
//		<td style='text-align:justify'>
//		<b>Cast In Gold</b></a><BR>
//		held up, headway over 2f out, every chance when carried left inside final furlong, unable to quicken near finish</td></tr>
//		<tr height=3 bgcolor="#FFFFFF"><td> </td></tr>
//		<tr height=14 bgcolor="#f5f5f5" onMouseOut="this.bgColor='#f5f5f5'" onMouseOver="this.bgColor='#66B821'">
//		<td style='text-align:justify'>

//		<b>Light Shift</b></a><BR>
//		chased leaders, ridden and every chance when hung left over 1f out, staying on same pace when not clear run inside final furlong</td></tr>
//		<tr height=3 bgcolor="#FFFFFF"><td> </td></tr>
//		<tr height=14 bgcolor="#f5f5f5" onMouseOut="this.bgColor='#f5f5f5'" onMouseOver="this.bgColor='#66B821'">
//		<td style='text-align:justify'>
//		<b>Free Offer</b></a><BR>
//		held up, headway over 1f out, not trouble leaders</td></tr>
//		<tr height=3 bgcolor="#FFFFFF"><td> </td></tr>

//		<tr height=14 bgcolor="#f5f5f5" onMouseOut="this.bgColor='#f5f5f5'" onMouseOver="this.bgColor='#66B821'">
//		<td style='text-align:justify'>
//		<b>Nicomedia</b></a><BR>
//		with winner, ridden from over 1f out, no extra</td></tr>
//		<tr height=3 bgcolor="#FFFFFF"><td> </td></tr>
//		<tr height=14 bgcolor="#f5f5f5" onMouseOut="this.bgColor='#f5f5f5'" onMouseOver="this.bgColor='#66B821'">
//		<td style='text-align:justify'>
//		<b>Look Who&acute;s Dancing</b></a><BR>
//		chased leaders, ridden over 2f out, weakened over 1f out</td></tr>

//		<tr height=3 bgcolor="#FFFFFF"><td> </td></tr>
//		<tr height=14 bgcolor="#f5f5f5" onMouseOut="this.bgColor='#f5f5f5'" onMouseOver="this.bgColor='#66B821'">
//		<td style='text-align:justify'>
//		<b>Split Briefs</b></a><BR>
//		chased leaders, bumped well over 1f out, soon weakened</td></tr>
//		<tr height=3 bgcolor="#FFFFFF"><td> </td></tr>
//		<tr height=14 bgcolor="#f5f5f5" onMouseOut="this.bgColor='#f5f5f5'" onMouseOver="this.bgColor='#66B821'">
//		<td style='text-align:justify'>
//		<b>Security Tiger</b></a><BR>

//		slowly into stride, soon prominent, hung left and weakened over 1f out</td></tr>
//		<tr height=3 bgcolor="#FFFFFF"><td> </td></tr>
//		<tr height=14 bgcolor="#f5f5f5" onMouseOut="this.bgColor='#f5f5f5'" onMouseOver="this.bgColor='#66B821'">
//		<td style='text-align:justify'>
//		<b>On The Go</b></a><BR>
//		slowly into stride, held up, hung left throughout, ridden over 2f out, soon weakened</td></tr>
//		<tr height=3 bgcolor="#FFFFFF"><td> </td></tr>
//		<tr height=14 bgcolor="#f5f5f5" onMouseOut="this.bgColor='#f5f5f5'" onMouseOver="this.bgColor='#66B821'">
//		<td style='text-align:justify'>

//		<b>Shanawa</b></a><BR>
//		started slowly, always in rear</td></tr>
//		<tr height=3 bgcolor="#FFFFFF"><td> </td></tr>
//		<tr height=14 bgcolor="#f5f5f5" onMouseOut="this.bgColor='#f5f5f5'" onMouseOver="this.bgColor='#66B821'">
//		<td style='text-align:justify'>
//		<b>Featherlight</b></a><BR>
//		held up, weakened 2f out</td></tr>
//		<tr height=3 bgcolor="#FFFFFF"><td> </td></tr>

//		<tr height=14 bgcolor="#f5f5f5" onMouseOut="this.bgColor='#f5f5f5'" onMouseOver="this.bgColor='#66B821'">
//		<td style='text-align:justify'>
//		<b>Summer Of Love</b></a><BR>
//		held up, ridden over 2f out, soon weakened</td></tr>
//		<tr height=3 bgcolor="#FFFFFF"><td> </td></tr>
//		</table>


//		<!--pf start-->
//		<table class="B2" WIDTH="770" align=center border=0 cellpadding=0 cellspacing=0>
//		<tr>

//		<td width=620 valign=top>
//		<div style="padding:4px;max-height:4px;min-height:4px;line-height:4px;height:4px;overflow:hidden;"> </div>
//		<table ALIGN=CENTER WIDTH=100% cellpadding=0 cellspacing=0 class="B2">
//		<tr height=14 bgcolor="#f5f5f5" onMouseOut="this.bgColor='#f5f5f5'" onMouseOver="this.bgColor='#A5BCD4'">
//		<td style='text-align:justify'>
//		<b>Granakey</b></a><BR>
//		mid-division, good headway over 1f out, stayed on well to lead inside final furlong</td></tr>
//		<tr height=3 bgcolor="#FFFFFF"><td> </td></tr>
//		<tr height=14 bgcolor="#f5f5f5" onMouseOut="this.bgColor='#f5f5f5'" onMouseOver="this.bgColor='#A5BCD4'">
//		<td style='text-align:justify'>

//		<b>Blakeshall Quest</b></a><BR>
//		made most, headed and no extra inside final furlong</td></tr>
//		<tr height=3 bgcolor="#FFFFFF"><td> </td></tr>
//		<tr height=14 bgcolor="#f5f5f5" onMouseOut="this.bgColor='#f5f5f5'" onMouseOver="this.bgColor='#A5BCD4'">
//		<td style='text-align:justify'>
//		<b>Danethorpe</b></a><BR>
//		in rear, headway on inside over 2f out, stayed on same pace final furlong</td></tr>
//		<tr height=3 bgcolor="#FFFFFF"><td> </td></tr>

//		<tr height=14 bgcolor="#f5f5f5" onMouseOut="this.bgColor='#f5f5f5'" onMouseOver="this.bgColor='#A5BCD4'">
//		<td style='text-align:justify'>
//		<b>Cree</b></a><BR>
//		chased leaders, soon driven along, stayed on same pace final 2f</td></tr>
//		<tr height=3 bgcolor="#FFFFFF"><td> </td></tr>
//		<tr height=14 bgcolor="#f5f5f5" onMouseOut="this.bgColor='#f5f5f5'" onMouseOver="this.bgColor='#A5BCD4'">
//		<td style='text-align:justify'>
//		<b>Four Amigos</b></a><BR>
//		chased leaders, faded approaching final furlong</td></tr>

//		<tr height=3 bgcolor="#FFFFFF"><td> </td></tr>
//		<tr height=14 bgcolor="#f5f5f5" onMouseOut="this.bgColor='#f5f5f5'" onMouseOver="this.bgColor='#A5BCD4'">
//		<td style='text-align:justify'>
//		<b>Far Note</b></a><BR>
//		chased leaders, weakened over 1f out</td></tr>
//		<tr height=3 bgcolor="#FFFFFF"><td> </td></tr>
//		<tr height=14 bgcolor="#f5f5f5" onMouseOut="this.bgColor='#f5f5f5'" onMouseOver="this.bgColor='#A5BCD4'">
//		<td style='text-align:justify'>
//		<b>Kissi Kissi</b></a><BR>

//		slowly into stride, headway on inner over 2f out, never near leaders</td></tr>
//		<tr height=3 bgcolor="#FFFFFF"><td> </td></tr>
//		<tr height=14 bgcolor="#f5f5f5" onMouseOut="this.bgColor='#f5f5f5'" onMouseOver="this.bgColor='#A5BCD4'">
//		<td style='text-align:justify'>
//		<b>Developer</b></a><BR>
//		in rear, never on terms</td></tr>
//		<tr height=3 bgcolor="#FFFFFF"><td> </td></tr>
//		<tr height=14 bgcolor="#f5f5f5" onMouseOut="this.bgColor='#f5f5f5'" onMouseOver="this.bgColor='#A5BCD4'">
//		<td style='text-align:justify'>

//		<b>Bond Puccini</b></a><BR>
//		mid-division on outer, effort on outer over 2f out, never a factor, lame</td></tr>
//		<tr height=3 bgcolor="#FFFFFF"><td> </td></tr>
//		<tr height=14 bgcolor="#f5f5f5" onMouseOut="this.bgColor='#f5f5f5'" onMouseOver="this.bgColor='#A5BCD4'">
//		<td style='text-align:justify'>
//		<b>Alistair John</b></a><BR>
//		mid-division, lost place over 3f out</td></tr>
//		<tr height=3 bgcolor="#FFFFFF"><td> </td></tr>

//		<tr height=14 bgcolor="#f5f5f5" onMouseOut="this.bgColor='#f5f5f5'" onMouseOver="this.bgColor='#A5BCD4'">
//		<td style='text-align:justify'>
//		<b>Stokesies Wish</b></a><BR>
//		held up, effort on wide outside over 2f out, never on terms</td></tr>
//		<tr height=3 bgcolor="#FFFFFF"><td> </td></tr>
//		<tr height=14 bgcolor="#f5f5f5" onMouseOut="this.bgColor='#f5f5f5'" onMouseOver="this.bgColor='#A5BCD4'">
//		<td style='text-align:justify'>
//		<b>Tancred Times</b></a><BR>
//		in touch, effort on outer over 2f out, no threat</td></tr>

//		<tr height=3 bgcolor="#FFFFFF"><td> </td></tr>
//		<tr height=14 bgcolor="#f5f5f5" onMouseOut="this.bgColor='#f5f5f5'" onMouseOver="this.bgColor='#A5BCD4'">
//		<td style='text-align:justify'>
//		<b>Obe Bold</b></a><BR>
//		reared start, always in rear</td></tr>
//		<tr height=3 bgcolor="#FFFFFF"><td> </td></tr>
//		<tr height=14 bgcolor="#f5f5f5" onMouseOut="this.bgColor='#f5f5f5'" onMouseOver="this.bgColor='#A5BCD4'">
//		<td style='text-align:justify'>
//		<b>Height Of Esteem</b></a><BR>

//		with leader, weakened over 2f out, eased and behind over 1f out</td></tr>
//		<tr height=3 bgcolor="#FFFFFF"><td> </td></tr>
//		</table>  </td>
//		<!--pf end-->
		
//		<tr height=14 bgcolor="#f5f5f5" onMouseOut="this.bgColor='#f5f5f5'" onMouseOver="this.bgColor='#A5BCD4'">
//		<td style='text-align:justify'>
//		  <b>Bold Bibi</b></a><BR>
//		  chased leaders, 4th entering straight, ridden to lead over 1f out, soon clear, eased close home</td></tr>
//		<tr height=3 bgcolor="#FFFFFF"><td> </td></tr>
		
//		<tr height=14 bgcolor="#f5f5f5" onMouseOut="this.bgColor='#f5f5f5'" onMouseOver="this.bgColor='#A5BCD4'">
//		<td style='text-align:justify'>
//		  <b>She&acute;s Our Mark</b></a><BR>
//		  chased leaders, 3rd halfway, ridden to challenge 2f out, led 1 1/2f out, headed under 1f out, no extra, kept on</td></tr>

		String strPattern="<td style='text-align:justify'>\\s+<b>(.*?)</b></a><BR>\\s+(.*?)</td>";
		Matcher matcher=CommonFun.GetMatcherStrGroup(content,strPattern);
		while(matcher.find())
		{
			String key=matcher.group(1).trim().replaceAll("&acute;", "'");
			String value=matcher.group(2).trim();
			if(key!=null&&key.length()>0)
				hp.put(key,value);	
		}		
		return hp;		
	}

	public static Vector getDivMoney(String content,String name,int num)
	{
//		<tr><td><b>TOTE </b> WIN &pound;13.50 PL &pound;3.90, &pound;8.80, &pound;6.40; Ex &pound;423.20; CSF &pound;519.51; TRICAST &pound;8775.33</td></tr>
//		<b>TOTE </b>toteswinger: 1&2 &pound;5.30, 1&3 &pound;15.90, 2&3 &pound;12.10. WIN &pound;5.20 PL &pound;2.00, &pound;3.00, &pound;3.00; Ex &pound;33.50; CSF &pound;29.65<br>There was no bid for the winner.   </div>

		Vector v = new Vector();
		String patter = name+" "+RaceCommonFun.moneyPatter;
		if(name.equals("PL")&&content.indexOf("Place ")>-1&&content.indexOf("PL ")<0){
			content=content.replaceAll("Place \\d{1,2} ","PL ");
			content=content.replaceAll(", PL ",",");
		}
		if(num>1)
			patter +="(, "+RaceCommonFun.moneyPatter+"){0,"+(num-1)+"}";

		Matcher matcher = CommonFun.GetMatcherStrGroup(content,patter);
		if(matcher.find())
		{
			content = matcher.group();
			matcher = CommonFun.GetMatcherStrGroup(content,moneyPatter);
			while(matcher.find())
				v.add(new BigDecimal(matcher.group(1)));
		}
		if (v.size()<num){
			for (int i=v.size();i<num;i++)
				v.add(new BigDecimal(0));
		}
		return v;
	}

//	public static Iterator GetQueryVector(String sql)
//	{
//		try
//		{
//			sql = sql.replaceAll("&lt;","<");
//			ZTStd myztstd = new ZTStd();
//			Vector v = myztstd.getVectorBySelect(sql);
//			if(v.size()>=3)
//			{
//				Iterator it = v.iterator();
//				if(it.hasNext()&&it.hasNext())
//				{
//					it.next();
//					it.next();
//				}
//				return it;
//			}
//		}catch(Exception e)
//		{
//			logger.error(e);
//		}
//		return null;
//	}

	public static String GetRawClass(String title)
	{
		String rawClassStr = "";
		String patter = "\\(.*?\\)";
		Matcher matcher = CommonFun.GetMatcherStrGroup(title,patter);
		while(matcher.find())
		{
			if(matcher.group().length()<60)
				rawClassStr +=matcher.group();
		}
		if(rawClassStr.length()>100)
			rawClassStr = rawClassStr.substring(0, 100);
		return rawClassStr;
	}

	public static Short GetDistance(Short distance)
	{
		if(distance!=null)
		{
			return new Short((new BigDecimal(distance.intValue()*0.9144).divide(new BigDecimal("1"),BigDecimal.ROUND_UP,0)).toString());
		}
		return null;
	}

//	public static CodeHeadGear GetHeadGearID(String sIn) {
//
//		String sHeadGearName = RaceCommonFun.GetTongueTie(sIn);
//		if (sHeadGearName != null && sHeadGearName.length() != 0)
//			return RaceCommonFun.GetCodeHeadGear(sHeadGearName);
//		return null;
//	}
//
	public static String GetNoOfFences4Post(String content)
	{
		String patter = "(\\d{1,2}) fences";
		Matcher matcher = CommonFun.GetMatcherStrGroup(content,patter);
		if(matcher.find())	
		{
//			prr.setTypeRace(((CodeRace)DBAccess.GetObjByValue(CodeRace.class,"Meaning","Chase")).getCode());
			return matcher.group(1).trim();
		}
		return null;
	}
	
	public static String GetNoOfFences4Pre(String content)
	{
		String patter = "(\\d{1,2}) fences ";
		Matcher matcher = CommonFun.GetMatcherStrGroup(content,patter);
		if(matcher.find())	
		{
			if(content.indexOf("last 2 fences")!=-1)
				return null;
			if(content.indexOf("final 2 fences")!=-1)
				return null;
//			prr.setTypeRace(((CodeRace)DBAccess.GetObjByValue(CodeRace.class,"Meaning","Chase")).getCode());
			return matcher.group(1).trim();
		}
		return null;
	}	

	public static String GetNoOfHurdle4Post(String content)
	{
//		<td width=40% ALIGN=RIGHT class="B1">11 hdles &nbsp;(2m6f110y) <B><BIG>2m6&frac12;f</BIG></B>&nbsp;</td>
//		title="always prominent, mistake 8th, challenged 3 out, hampered last 2 fences, no extra flat">
		
		String patter = "(\\d{1,2}) hdles";
		Matcher matcher = CommonFun.GetMatcherStrGroup(content,patter);
		if(matcher.find())	
		{
//			prr.setTypeRace(((CodeRace)DBAccess.GetObjByValue(CodeRace.class,"Meaning","Hurdle")).getCode());
			return matcher.group(1).trim();
		}
		return null;
	}

	public static String GetNoOfHurdle4Pre(String content)
	{
		try {
			
//            data-test-selector="RC-headerBox__stalls">
//            <div class="RC-headerBox__infoRow__name">No. of hurdles:</div>
//            <div class="RC-headerBox__infoRow__content">8</div>
//        </div>
			
		String patter = "No. of hurdles:</div>.*?>(\\d{1,2})<";
		Matcher matcher = CommonFun.GetMatcherStrGroup(content,patter);
		if(matcher.find())	
		{
//			prr.setTypeRace(((CodeRace)DBAccess.GetObjByValue(CodeRace.class,"Meaning","Hurdle")).getCode());
			return matcher.group(1).trim();
		} else {			
			patter = "(\\d{1,2}) hurdles";
			matcher = CommonFun.GetMatcherStrGroup(content,patter);
			if(matcher.find())	
			{
				return matcher.group(1).trim();
			}			
		}
		} catch (Exception e) {
			logger.error("", e);
			return null;
		}
		return null;
	}
//
//	public static Byte GetFenceOmitted(String content)
//	{
////		<td width=40% ALIGN=RIGHT class="B1">10 fences 2 omitted &nbsp;(2m1f110y) <B><BIG>2m1&frac12;f</BIG></B>&nbsp;</td>
//		String patter = "(\\d{1,2}) omitted";
//		Matcher matcher = CommonFun.GetMatcherStrGroup(content,patter);
//		if(matcher.find())	
//			return new Byte(matcher.group(1).trim());
//		return null;
//	}
//
//	public static String GetTongueTie(String sIn) {
////		<td class="B5"><b>&nbsp;10-4</b> <IMG SRC="/images/furniture/oh.gif" ALT="out of handicap">9v</td>
////		<td class="B5"><b>&nbsp;10-3</b> <IMG SRC="/images/furniture/ow.gif" ALT=overweight>10e/s</td>
////		<td class="B5"><b>&nbsp;8-13</b> e/sp<span class='B7'>1</span></td>
////		<td class="B5"><b>&nbsp;10-4</b> <IMG SRC="/images/furniture/oh.gif" ALT="out of handicap">9v</td>
////		<td class="B5"><b>&nbsp;8-1</b> <IMG SRC="/images/furniture/oh.gif" ALT="out of handicap">10hp<span class='B7'>1</span></td>
////		<td class="B5"><b>&nbsp;10-0</b> tb</td>
//		
////		<td class="black">8-13&nbsp;p</td>
////		<td class="black">8-1<img src="/images/furniture/ico/oh.gif" class="wgtIco" alt="over weight" />1&nbsp;v</td>
////		<td class="black">11-0&nbsp;tb</td>
//		
////		<span class="gr">p</span>
////		<span class="gr">b<sup>1<sup></span>
//		
////		<td class="nowrap black"><span>8-5<img src="/img/ico/ico-weight-ow.gif" class="wgtIco" alt="over weight" />1&nbsp;<span class="lightGray"></span></span></td>
//
////		<td class="nowrap black"><span>9-3&nbsp;<span class="lightGray">p</span></span></td>		
////		<td class="nowrap black"><span>9-2&nbsp;<span class="lightGray">v</span></span></td>
////		<td class="nowrap black"><span>8-11&nbsp;<span class="lightGray">p</span></span></td>
////		<td class="nowrap black"><span>8-9&nbsp;<span class="lightGray">p</span></span></td>
//
////		<td class="nowrap black"><span>8-5&nbsp;<span class="lightGray">b<sup>1</sup></span></span></td>
////		 <td class="nowrap black"><span>11-6&nbsp;<span class="lightGray">b<sup>1</sup></span></span></td>
//
//
//		
////		String patter = "([a-z]{1,5})(/[a-z]{1,2})?(<span class='B7'>(\\d{1,2})</span>)?$";
//		
//		String patter = "<span class=\"lightGray\">(.*?)(<sup>(\\d{1,2})</?sup>)?\\s?</span>";
//		Matcher matcher = CommonFun.GetMatcherStrGroup(sIn,patter);
//		if(matcher.find()){
//			String heargear = matcher.group(1).trim();
//			String num = "";			
//			if(matcher.group(2)!=null)
//				num = matcher.group(3);
//			if (heargear.length() > 0)
//				return heargear.trim()+num;
//		}
//		
//		patter = "<span class=\"gr\">(.*?)</span>";
//		matcher = CommonFun.GetMatcherStrGroup(sIn,patter);
//		if(matcher.find()){
//			String heargear = matcher.group(1).replaceAll("<sup>", "");
//			return heargear;
//		}
//		return null;
//	}
//
//	public static CodeTrackCondition GetCodeTrackCondition(String tcname)
//	{
//		if(tcname==null || tcname.equals(""))
//			return null;
//		Object obj = DBAccess.GetObjByValue(CodeTrackCondition.class,"TrackConditionName", tcname);
//		if(obj!=null)
//			return (CodeTrackCondition)obj;
//		else
//		{
//			CodeTrackCondition ctc = new CodeTrackCondition();
//			ctc.setTrackConditionName(tcname);
//			obj = DBAccess.GetMaxIDObj(CodeTrackCondition.class.getName(),"TrackConditionId");
//			if(obj!=null)
//				ctc.setTrackConditionId(new Byte(((CodeTrackCondition)obj).getTrackConditionId().intValue()+1+""));
//			else
//				ctc.setTrackConditionId(new Byte("1"));
//			if(ctc.getTrackConditionId().intValue()>127)
//				return null;
//			return (CodeTrackCondition)DBAccess.create(ctc);						
//		}		
//	}
//
	public static String GetGear(String sName)
	{
		try {
			if (sName == null || sName.equals("")) return null;
			
			String name = sName;
			if (name.endsWith("1")) name = name.substring(0, name.length()-1);
			
			String sql = "select HeadGearID from Code_HeadGear where HeadGearName = '" + name + "'";
			String HeadGearID = CommonBll.GetColumn(sql);
			if (HeadGearID != null) {
				return HeadGearID;
			}
			
			sql = "select max(HeadGearID) from Code_HeadGear";
			ResultSet rs = new ZTStd().getResultBySelect(sql);
			if (!rs.next()){
				return null;
			}
			
			Integer maxId = rs.getInt(1)+1; 
			sql = "insert into Code_HeadGear(HeadGearID,HeadGearName) values("+maxId + ",'" + name+ "')";
			logger.info("Insert new Code_HeadGear: " + sql);
			new ZTStd().execSQL(sql);
			return maxId+"";
			
		} catch (Exception e) {
			logger.error("", e);
			return null;
		}
	}
	
	
	public static String GetSexID(String sName)	{
		try {
			
			if (sName == null || sName.equals("")) return null;
			
			String name = sName.replaceAll("'", "''");
			
			String sql = "Select SexID from code_sex where SexName = '" + name + "'";
			String HeadGearID = CommonBll.GetColumn(sql);
			if (HeadGearID != null) {
				return HeadGearID;
			}
			
			sql = "select max(SexID) from code_sex";
			ResultSet rs = new ZTStd().getResultBySelect(sql);
			if (!rs.next()){
				return null;
			}
			
			Integer maxId = rs.getInt(1)+1; 
			sql = "insert into code_sex(SexID,SexName) values("+maxId + ",'" + name+ "')";
			logger.info("Insert new code_sex: " + sql);
			new ZTStd().execSQL(sql);
			return maxId+"";
			
		} catch (Exception e) {
			logger.error("", e);
			return null;
		}
	}
	
	public static String getBreederID(String sName)	{
		try {
			
			if (sName == null || sName.equals("")) return null;
			
			String name = sName.replaceAll("'", "''");
			
			String sql = "Select BreederID from Code_Breeder where BreederName = '" + name + "'";
			String HeadGearID = CommonBll.GetColumn(sql);
			if (HeadGearID != null) {
				return HeadGearID;
			}
			
			sql = "select max(BreederID) from Code_Breeder";
			ResultSet rs = new ZTStd().getResultBySelect(sql);
			if (!rs.next()){
				return null;
			}
			
			Integer maxId = rs.getInt(1)+1; 
			sql = "insert into Code_Breeder(BreederID,BreederName) values("+maxId + ",'" + name+ "')";
			logger.info("Insert new Code_Breeder: " + sql);
			new ZTStd().execSQL(sql);
			return maxId+"";
			
		} catch (Exception e) {
			logger.error("", e);
			return null;
		}
	}
	
	public static String getHorseOriginID(String sName)	{
		try {
			
			if (sName == null || sName.equals("")) return null;
			
			String name = sName.replaceAll("'", "''");
			
			String sql = "Select HorseOriginID from Code_HorseOrigin where HorseOriginCode = '" + name + "'";
			String HeadGearID = CommonBll.GetColumn(sql);
			if (HeadGearID != null) {
				return HeadGearID;
			}
			
			sql = "select max(HorseOriginID) from Code_HorseOrigin";
			ResultSet rs = new ZTStd().getResultBySelect(sql);
			if (!rs.next()){
				return null;
			}
			
			Integer maxId = rs.getInt(1)+1; 
			sql = "insert into Code_HorseOrigin(HorseOriginID,HorseOriginCode) values("+maxId + ",'" + name+ "')";
			logger.info("Insert new Code_HorseOrigin: " + sql);
			new ZTStd().execSQL(sql);
			return maxId+"";
			
		} catch (Exception e) {
			logger.error("", e);
			return null;
		}
	}	

	public static String GetRaceId(String myUrl) {
		String patterStr = "race_id=(\\d{1,9})";
		Matcher myMatcher = CommonFun.GetMatcherStrGroup(myUrl, patterStr);
		if(myMatcher.find())
			return myMatcher.group(1);
		else
			return null;
	}

	public static String GetHorseId(String myUrl) {
		String patterStr = "horse_id=(\\d{1,9})\\s+";
		Matcher myMatcher = CommonFun.GetMatcherStrGroup(myUrl, patterStr);
		if(myMatcher.find())
			return myMatcher.group(1);
		else
			return null;
	}

//	public static CodeRaceSort GetCodeRaceSort(String title) {
//		if (title.indexOf("Hurdle") != -1)
//			return (CodeRaceSort)DBAccess.GetObjByValue(CodeRaceSort.class,"RaceSortName", "Hurdle");
//		else if (title.indexOf("Chase") != -1)
//			return (CodeRaceSort)DBAccess.GetObjByValue(CodeRaceSort.class,"RaceSortName", "Chase");
//		else
//			return (CodeRaceSort)DBAccess.GetObjByValue(CodeRaceSort.class,"RaceSortName", "Flat");
//	}
//
//	public static CodeClass GetCodeClass(String title) {
////		<td width=88% class="H3">Gosforth Decorating And Building Services Maiden Auction Stakes (6)<span class='B7'>&nbsp;(2yo)</span></td>
//		String strPattern = "\\((\\d)\\)";
//		Matcher matcher = CommonFun.GetMatcherStrGroup(title, strPattern);
//		if (matcher.find())
//			return RaceCommonFun.GetCodeClassFromName(matcher.group(1));
//		strPattern = "\\([A-Z](\\d)\\)";
//		matcher = CommonFun.GetMatcherStrGroup(title, strPattern);
//		if (matcher.find())
//			return RaceCommonFun.GetCodeClassFromName(matcher.group(1));
////		European Breeders Fund Maiden Stakes (Class 5)(2yo)
//		strPattern = "\\(Class (\\d)\\)";
//		matcher = CommonFun.GetMatcherStrGroup(title, strPattern);
//		if (matcher.find())
//			return RaceCommonFun.GetCodeClassFromName(matcher.group(1));	
//		strPattern = "\\(Class ([A-Z])\\)";
//		matcher = CommonFun.GetMatcherStrGroup(title, strPattern);
//		if (matcher.find())
//		{
//			char ch = matcher.group(1).charAt(0);
//			return RaceCommonFun.GetCodeClassFromName(String.valueOf((char)(ch-16)));
//		}	
//		strPattern = "\\(Group (\\d)\\)";
//		matcher = CommonFun.GetMatcherStrGroup(title, strPattern);
//		if (matcher.find())
//			return RaceCommonFun.GetCodeClassFromName("1");	
//		strPattern = "Listed";
//		matcher = CommonFun.GetMatcherStrGroup(title, strPattern);
//		if (matcher.find())
//			return RaceCommonFun.GetCodeClassFromName("1");	
//		return null;
//	}
//
//	public static CodeClass GetCodeClassFromName(String classname)
//	{
//		Object obj = DBAccess.GetObjByValue(CodeClass.class, "ClassName",classname);
//		if(obj!=null)
//			return (CodeClass)obj;
//		else
//		{
//			CodeClass cc = new CodeClass();
//			int maxid = 1;
//			obj = DBAccess.GetMaxIDObj(CodeClass.class.getName(), "ClassId");
//			if(obj!=null)
//				maxid = ((CodeClass)obj).getClassId().intValue()+1;
//			cc.setClassId(new Byte(maxid+""));
//			cc.setClassName(classname);
//			return (CodeClass)DBAccess.create(cc);
//		}
//	}

	public static String GetTypeWeight(String title)
	{
		String sql = "select Code from Code_Weight where Meaning = '";
		title = title.toLowerCase();
		if (title.indexOf("handicap") != -1 || title.indexOf("hcap") != -1 || title.indexOf("h''cap") != -1 || title.indexOf("nursery") != -1)
			return CommonBll.GetColumn(sql+"Handicap'");
		if (title.indexOf("auction race") != -1 || title.indexOf("listed") != -1 || title.indexOf("stake") != -1 || title.indexOf("group") != -1)
			return CommonBll.GetColumn(sql+"Special'");
		if (title.indexOf("maiden") != -1 || title.indexOf("novice ") != -1)
			return CommonBll.GetColumn(sql+"Set'");
		if (title.indexOf("claim") != -1 || title.indexOf("selling") != -1 || title.indexOf("sale") != -1)
			return CommonBll.GetColumn(sql+"Claim'");
		return null;
	}

	public static String GetTypeRace(String title)
	{
		String TypeRaceName = null;
		String sql = "select Code from Code_Race where Meaning = '";
		if(title!=null)
		{
			title = title.toLowerCase();
			if (title.indexOf("National Hunt Flat".toLowerCase()) != -1 
					|| title.indexOf("I.N.H. Flat".toLowerCase()) != -1 
					|| title.indexOf("NH Flat".toLowerCase()) != -1
					|| title.indexOf("N H Flat".toLowerCase()) != -1 
					|| title.indexOf("Celebrity Flat Race".toLowerCase()) != -1 
					|| title.indexOf("National Hunt Race Flat".toLowerCase()) != -1
					|| (title.indexOf("Bumper".toLowerCase()) != -1 && !title.startsWith("Bumper".toLowerCase()))) {
				
				TypeRaceName = "NH Flat";
				return CommonBll.GetColumn(sql+TypeRaceName+"'");
			}
			if (title.indexOf(" chase ") != -1
					|| title.indexOf(" chase(") != -1
					|| title.endsWith(" chase")
					|| title.indexOf(" chase)") != -1
					|| title.indexOf("(chase)") != -1
					|| title.startsWith("chase ")
					|| title.indexOf("'chase") != -1
					|| title.indexOf("(chase ") != -1
			) {
				TypeRaceName = "Chase";
				return CommonBll.GetColumn(sql+TypeRaceName+"'");				
			}
			if (title.indexOf("steeplechase") != -1 && title.indexOf(" hurdle ") == -1)
			{
				String patter1 = "\".*?steeplechase.*?\"";
				String patter2 = "'.*?steeplechase.*?'";
				Matcher matcher1 = CommonFun.GetMatcherStrGroup(title,patter1);
				Matcher matcher2 = CommonFun.GetMatcherStrGroup(title,patter2);				
				if(!matcher1.find()&&!matcher2.find()) {
					TypeRaceName = "Chase";
					return CommonBll.GetColumn(sql+TypeRaceName+"'");					
				}
			}
			if (title.indexOf(" Hurdle".toLowerCase()) != -1||title.indexOf(" Hdl".toLowerCase()) != -1||title.indexOf(" H'dle".toLowerCase()) != -1||title.indexOf("(Hurdle)".toLowerCase()) != -1||title.indexOf("(Hurdles)".toLowerCase()) != -1) {
				TypeRaceName = "Hurdle";
				return CommonBll.GetColumn(sql+TypeRaceName+"'");				
			}
		}
		
		if (TypeRaceName == null) TypeRaceName = "Flat";
		
		return CommonBll.GetColumn(sql+TypeRaceName+"'");
	}
	
	public static String getNewTypeRace4Pre(String TypeRace, String Distance, String racetitle, String rawclass, String RaceAreaId, String ClothNo_1_Draw, String ClothNo_2_Draw) {
		String newTypeRace = TypeRace;
		
		try {
			
			int dDistance = Integer.parseInt(Distance);
			boolean hasDraw1 = false;
			boolean hasDraw2 = false;
			
			if (ClothNo_1_Draw != null) {
				hasDraw1 = true;
				newTypeRace = CommonBll.GetTypeRaceCode("Flat");
			}
	
			
			if (ClothNo_2_Draw != null) {
				hasDraw2 = true;
			}
			
//			if (ClothNo_2_Draw != null) {
				if (hasDraw1 || hasDraw2) {
					newTypeRace = CommonBll.GetTypeRaceCode("Flat");
				} else {
					if (TypeRace != null
							&& TypeRace.equals("1")
							&& ClothNo_2_Draw == null
							&& dDistance > 3000) {
						newTypeRace = CommonBll.GetTypeRaceCode("NH Flat");
					}
					
					if (TypeRace != null
							&& TypeRace.equals("1")
							&& ClothNo_2_Draw == null) {

						// r.rawclass like '%(q.r.)%' or r.rawclass like
						// '%(qr)%' or r.rawclass like '%(q.r)%' --QR, Q.R,
						// Q.R. Races
						// or r.rawclass like '%pro-am%' or r.rawclass like
						// '%pro/am%' --Professional/Amateur Races
						// //or r.title like '%bumper%' --"Bumper" races -
						// suspect same as QR/Pro-Am
						// or r.rawclass like '%(INH)%' or r.title like
						// '%national hunt%' --INH / NH
						// or r.title like '% hurde(%' or r.title like '%
						// hurlde %' --mis-spelt Hurdle races need to be
						// classified as hurdles
						// or r.title like '%ladies handicap%' or r.title
						// like '%ladies derby handicap%' or r.title like
						// '%(ladies)%' or r.title like '%ladies flat race%'
						// --these all seem to be non-flat races
						racetitle = racetitle.toLowerCase();
						rawclass = rawclass.toLowerCase();
						if (rawclass.indexOf("(q.r.)") != -1
								|| rawclass.indexOf("(qr)") != -1
								|| rawclass.indexOf("(q.r)") != -1
								|| rawclass.indexOf("pro-am") != -1
								|| rawclass.indexOf("pro/am") != -1
								|| rawclass.indexOf("(INH)".toLowerCase()) != -1
								|| racetitle.indexOf("national hunt") != -1
								|| racetitle.indexOf(" hurde(") != -1
								|| racetitle.indexOf(" hurlde ") != -1
								|| racetitle.indexOf("ladies handicap") != -1
								|| racetitle
										.indexOf("ladies derby handicap") != -1
								|| racetitle.indexOf("(ladies)") != -1
								|| racetitle.indexOf("ladies flat race") != -1) {
							newTypeRace = CommonBll.GetTypeRaceCode("NH Flat");
						} else { //if (this.isCurrentDay(myprr.getRaceDate())) {
							String title = "uk prerace type_race = flat and draw is null warn!!!";
							logger.warn(title);
//							StringBuffer sb = new StringBuffer("raceid="
//									+ myprr.getRaceId().toString()
//									+ ",racedate="
//									+ myprr.getRaceDate().toString()
//									+ ",trackid ="
//									+ myprr.getCodeTrack().getTrackId()
//											.toString() + "("
//									+ myprr.getCodeTrack().getTrackName()
//									+ ")");
//							if (!UKControler1.vPreWarn.contains(myprr
//									.getRaceId().toString())) {
//								UKControler1.vPreWarn.add(myprr.getRaceId()
//										.toString());
//								new MailSend().sendMail(title, sb);
//							}
						}
					}
				}
//			}
			
						
		} catch (Exception e) {
			logger.error("",e);
		}
		return newTypeRace;
	}
	
	
	public static String getNewTypeRace4Post(String TypeRace, String Distance, String racetitle, String rawclass, String RaceAreaId, String FinishPos_1_Draw) {
		
		
		String newTypeRace = TypeRace;
		try {

			int dDistance = Integer.parseInt(Distance);
			
			if (TypeRace != null && !TypeRace.equals("1") && TypeRace != null) {
				newTypeRace = CommonBll.GetTypeRaceCode("Flat");
			}
			if (TypeRace != null && TypeRace.equals("1") && FinishPos_1_Draw == null && dDistance>3000) {
				newTypeRace = CommonBll.GetTypeRaceCode("NH Flat");
			}
			
			if (TypeRace != null && !TypeRace.equals("1") && FinishPos_1_Draw != null) {
				newTypeRace = "1";
			}

			if (TypeRace != null && !TypeRace.equals("1") && FinishPos_1_Draw != null) {
				newTypeRace = CommonBll.GetTypeRaceCode("Flat");
			}
			
			if(!RaceAreaId.equals("6") && TypeRace != null && TypeRace.equals("1") && FinishPos_1_Draw == null)
			{
//				r.rawclass like '%(q.r.)%' or r.rawclass like '%(qr)%' or r.rawclass like '%(q.r)%' --QR, Q.R, Q.R. Races
//				or r.rawclass like '%pro-am%' or r.rawclass like '%pro/am%' --Professional/Amateur Races
//				//or r.title like '%bumper%' --"Bumper" races - suspect same as QR/Pro-Am 
//				or r.rawclass like '%(INH)%' or r.title like '%national hunt%' --INH / NH 
//				or r.title like '% hurde(%' or r.title like '% hurlde %' --mis-spelt Hurdle races need to be classified as hurdles 
//				or r.title like '%ladies handicap%' or r.title like '%ladies derby handicap%' or r.title like '%(ladies)%' or r.title like '%ladies flat race%' --these all seem to be non-flat races
				racetitle = racetitle.toLowerCase();
				rawclass = rawclass.toLowerCase();
				if(rawclass.indexOf("(q.r.)")!=-1||rawclass.indexOf("(qr)")!=-1||rawclass.indexOf("(q.r)")!=-1||rawclass.indexOf("pro-am")!=-1||rawclass.indexOf("pro/am")!=-1||rawclass.indexOf("(INH)".toLowerCase())!=-1||racetitle.indexOf("national hunt")!=-1||racetitle.indexOf(" hurde(")!=-1||racetitle.indexOf(" hurlde ")!=-1||racetitle.indexOf("ladies handicap")!=-1||racetitle.indexOf("ladies derby handicap")!=-1||racetitle.indexOf("(ladies)")!=-1||racetitle.indexOf("ladies flat race")!=-1)
				{
					newTypeRace = CommonBll.GetTypeRaceCode("NH Flat");
				}
				else
				{
					String title = "uk postrace type_race = flat and draw is null warn!!!";
					logger.warn(title);
//					StringBuffer sb = new StringBuffer("raceid="+prr.getRaceId().toString()+",racedate="+prr.getRaceDate().toString()+",trackid ="+prr.getCodeTrack().getTrackId().toString()+"("+prr.getCodeTrack().getTrackName()+")");
//					if(!UKControler1.vPostWarn.contains(prr.getRaceId().toString()))
//					{
//						UKControler1.vPostWarn.add(prr.getRaceId().toString());
//						try {
//							new MailSend().sendMail(title,sb);
//						} catch (Exception e) {
//							logger.error("send email faild");
//						}
//
//					}
				}
			}
			
		} catch (Exception e) {
			logger.error("",e);
		}
		
		return newTypeRace;
	}
	
	

	public static Date getActualDateTime(String content)
	{
		try
		{
			Date yyyymmdd = RaceCommonFun.getRaceDate(content);
			if(yyyymmdd==null)return null;
			String startTime = RaceCommonFun.getOfficeStartTime(content);
			DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
			DateFormat df1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String racedate = df.format(yyyymmdd)+" "+startTime;		
			return df1.parse(racedate);			
		}
		catch(Exception e)
		{
			logger.error(e);
		}
		return null;
	}
	
	public static Date getActualDateTime(String content,boolean isFr)
	{
		try
		{
			Date yyyymmdd = RaceCommonFun.getRaceDate(content);
			if(yyyymmdd==null)return null;
			String startTime = RaceCommonFun.getOfficeStartTime(content,true);
			DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
			DateFormat df1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String racedate = df.format(yyyymmdd)+" "+startTime;		
			return df1.parse(racedate);			
		}
		catch(Exception e)
		{
			logger.error(e);
		}
		return null;
	}

//	public static Date getActualDateTime(String content,PostRaceRace prr)
//	{
//		try
//		{
//			Date yyyymmdd = prr.getRaceDate();
//			if(yyyymmdd==null)return null;
//			String startTime = RaceCommonFun.getOfficeStartTime(content);
//			DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
//			DateFormat df1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//			String racedate = df.format(yyyymmdd)+" "+startTime;		
//			return df1.parse(racedate);			
//		}
//		catch(Exception e)
//		{
//			logger.error(e);
//		}
//		return null;
//	}
//	
//	public static Date getActualDateTime(String content,PostRaceRace prr,boolean isFr)
//	{
//		try
//		{
//			Date yyyymmdd = prr.getRaceDate();
//			if(yyyymmdd==null)return null;
//			String startTime = RaceCommonFun.getOfficeStartTime(content,true);
//			DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
//			DateFormat df1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//			String racedate = df.format(yyyymmdd)+" "+startTime;		
//			return df1.parse(racedate);			
//		}
//		catch(Exception e)
//		{
//			logger.error(e);
//		}
//		return null;
//	}
//
//	public static Date getScheduledDateTime(String content,PostRaceRace prr)
//	{
//		try
//		{
//			Date yyyymmdd = prr.getRaceDate();
//			if(yyyymmdd==null)return null;
//			String startTime = RaceCommonFun.getStartTime(content);
//			DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
//			DateFormat df1 = new SimpleDateFormat("yyyy-MM-dd HH:mm");
//			String racedate = df.format(yyyymmdd)+" "+startTime;		
//			return df1.parse(racedate);			
//		}
//		catch(Exception e)
//		{
//			logger.error(e);
//		}
//		return null;
//	}
//	
//	public static Date getScheduledDateTime(String content,PostRaceRace prr,boolean isFr)
//	{
//		try
//		{
//			Date yyyymmdd = prr.getRaceDate();
//			if(yyyymmdd==null)return null;
//			String startTime = RaceCommonFun.getStartTime(content,true);
//			DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
//			DateFormat df1 = new SimpleDateFormat("yyyy-MM-dd HH:mm");
//			String racedate = df.format(yyyymmdd)+" "+startTime;		
//			return df1.parse(racedate);			
//		}
//		catch(Exception e)
//		{
//			logger.error(e);
//		}
//		return null;
//	}

	public static Date getScheduledDateTime(String content)
	{
		try
		{
			Date yyyymmdd = RaceCommonFun.getRaceDate(content);
			if(yyyymmdd==null)return null;
			String startTime = RaceCommonFun.getStartTime(content);
			DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
			DateFormat df1 = new SimpleDateFormat("yyyy-MM-dd HH:mm");
			String racedate = df.format(yyyymmdd)+" "+startTime;	
			return df1.parse(racedate);			
		}
		catch(Exception e)
		{
			logger.error(e);
		}
		return null;
	}
	
	public static Date getScheduledDateTime(String content,boolean isFr)
	{
		try
		{
			Date yyyymmdd = RaceCommonFun.getRaceDate(content);
			if(yyyymmdd==null)return null;
			String startTime = RaceCommonFun.getStartTime(content,true);
			DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
			DateFormat df1 = new SimpleDateFormat("yyyy-MM-dd HH:mm");
			String racedate = df.format(yyyymmdd)+" "+startTime;	
			return df1.parse(racedate);			
		}
		catch(Exception e)
		{
			logger.error(e);
		}
		return null;
	}

//	public static Date getScheduledDateTime(String content,PreRaceRace prr)
//	{
//		try
//		{
//			Date yyyymmdd = prr.getRaceDate();
//			if(yyyymmdd==null)return null;
//			String startTime = prr.getScheduledStartTime();
//			if(startTime==null)
//			{
//				logger.error("preracerace raceid="+prr.getRaceId()+" page starttime not find");
//				return null;
//			}
//			DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
//			DateFormat df1 = new SimpleDateFormat("yyyy-MM-dd HH:mm");
//			String racedate = df.format(yyyymmdd)+" "+startTime;		
//			return df1.parse(racedate);			
//		}
//		catch(Exception e)
//		{
//			logger.error(e);
//		}
//		return null;
//	}
	
	public static String getOfficeStartTime(String content,boolean isFR)
	{
//		<td width=12% align=center>[off 4:46]</td>

//		<table width='595' border=0 cellspacing=0 cellpadding=0>
//		<tr>
//		<td width=55 VALIGN=TOP class="H1">5:20</td>
//		<td width=540 colspan=2 class="H3">Winged Love (Pro/Am) INH Flat Race (Div II)<span class='B7'>&nbsp;(4yo+)</span></td>
//		</tr>
//		</table>
//		</td>
//		</tr>
//		<tr><td>
//		<table width='595' border=0 cellspacing=0 cellpadding=0>
//		<tr class="B2" style="color:#808080;font-weight:bold;">
//		<td width=55 valign=top>[off 5:20]</td>
//		<td width=350 valign=top>&pound;3,812.41, &pound;888.28, &pound;391.72, &pound;226.21</td>
//		<td class="B1" align=right>&nbsp; <B><BIG>2m</BIG></B>&nbsp;&nbsp;</td>
//		</tr>
//		</table>

//		<td width=55 valign=top style="color:#808080;font-weight:bold;">[off 1:05]</td>

		String patter = "\\[off (\\d{1,2}):(\\d{2})\\]";
		Matcher matcher = CommonFun.GetMatcherStrGroup(content,patter);
		if(matcher.find())
		{
			String hour = matcher.group(1);
			if(hour.length()<2)
				hour = "0"+hour;
			String minute = matcher.group(2);
			return hour+":"+minute+":00";
		}
		return "00:00:00";		
	}

	public static String getOfficeStartTime(String content)
	{
//		<td width=12% align=center>[off 4:46]</td>

//		<table width='595' border=0 cellspacing=0 cellpadding=0>
//		<tr>
//		<td width=55 VALIGN=TOP class="H1">5:20</td>
//		<td width=540 colspan=2 class="H3">Winged Love (Pro/Am) INH Flat Race (Div II)<span class='B7'>&nbsp;(4yo+)</span></td>
//		</tr>
//		</table>
//		</td>
//		</tr>
//		<tr><td>
//		<table width='595' border=0 cellspacing=0 cellpadding=0>
//		<tr class="B2" style="color:#808080;font-weight:bold;">
//		<td width=55 valign=top>[off 5:20]</td>
//		<td width=350 valign=top>&pound;3,812.41, &pound;888.28, &pound;391.72, &pound;226.21</td>
//		<td class="B1" align=right>&nbsp; <B><BIG>2m</BIG></B>&nbsp;&nbsp;</td>
//		</tr>
//		</table>

//		<td width=55 valign=top style="color:#808080;font-weight:bold;">[off 1:05]</td>

		String patter = "\\[off (\\d{1,2}):(\\d{2})\\]";
		Matcher matcher = CommonFun.GetMatcherStrGroup(content,patter);
		if(matcher.find())
		{
			String hour = matcher.group(1);
			if(Integer.parseInt(hour)<11)
				hour = Integer.parseInt(hour)+12+"";
			String minute = matcher.group(2);
			return hour+":"+minute+":00";
		}
		return "00:00:00";		
	}

	public static Date getRaceDate(String content)
	{

//		<tr><td width=70% ALIGN=CENTER>CLONMEL, 03 Feb 2005</td></tr>
//		<div align=center class="H1">HAMILTON <span class="B1" align="center"><b>(18 Sep 2005)</b></span></div>
//		<div align=center class="H1">BRIGHTON <span class="B1" align="center"><b>(03 Oct 2005)</b></span></div>
//		r_date=2005-10-21&flag=1&cards=1" style="background-color:#FFFFFF;"  selected>
//		<option value="/horses/detailed_card.sd?race_id=398945&r_date=2006-2-7&flag=1&cards=1" style="background-color:#FFFFFF;" class="B2"  selected>1:20 - 2m1&frac12;f Hurdle Cls  4</option>
//		<div align=center class="H1">LINGFIELD (A.W) <span class="B1" align="center"><b>(08 Feb 2006)</b></span></div>

//		<tr>
//		<td class="B11" valign=top align=center><font color=#333333>
//		<span class="H24"><b>DOWN ROYAL</b></span><BR>
//		<span class="B11">01&nbsp; May&nbsp;2006</span><BR>
//		<span class="B12"><b>Good To Yielding</b></span>
//		</td>
//		</tr>

//		<td class="B11" valign=top align=center><font color=#333333>
//		<span class="H24"><b>NEWMARKET (JULY)</b></span><BR>
//		<span class="B11">21&nbsp; July&nbsp;2006</span><BR>

//		<span class="B12"><b>Good To Firm</b></span>
//		</td>

//		<tr>
//		<td class="B11" valign=top align=center><font color=#333333>
//		<span class="H24"><b>SOUTHWELL (A.W)</b></span><BR>
//		<span class="B11">02&nbsp; November&nbsp;2006</span><BR>

//		<span class="B12"><b>Standard</b></span>
//		</td>
//		</tr>

		try
		{
			DateFormat df = new SimpleDateFormat("yyyy-M-d");
			String patter = "\\(?(\\d{1,2} [A-Z][a-z]{2} \\d{4})\\)?";
			String patter1 = "r_date=(\\d{4}-\\d{1,2}-\\d{1,2})&flag=\\d&cards=\\d\" style=\"background-color:#FFFFFF;\" class=\"B2\"  selected>";
			String patter2 = "r_date=(\\d{4}-\\d{1,2}-\\d{1,2})&flag=\\d&cards=\\d\" style=\"background-color:#FFFFFF;\"  selected>";

			String patter3 = "<span class=\"B11\">(.*?)</span><BR>";

			Matcher matcher = CommonFun.GetMatcherStrGroup(content,patter3);
			if(matcher.find())
			{
				String racedate = matcher.group(1);
				racedate = racedate.replaceAll("&nbsp;"," ");
				racedate = racedate.replaceAll("  "," ");
				return new Date(racedate);
			}
			matcher = CommonFun.GetMatcherStrGroup(content,patter1);
			if(matcher.find())
			{
				return df.parse(matcher.group(1));
			}
			matcher = CommonFun.GetMatcherStrGroup(content,patter2);
			if(matcher.find())
			{
				return df.parse(matcher.group(1));
			}

			matcher = CommonFun.GetMatcherStrGroup(content,patter3);
			if(matcher.find())
			{
				String val = matcher.group(1).replaceAll("&nbsp;"," ");
				val = val.replaceAll("  "," ");
				String patterb = "(\\d{1,2}\\s*[a-zA-Z]{4}\\s*\\d{4})";
				matcher = CommonFun.GetMatcherStrGroup(val,patterb);
				if (matcher.find())
					return new Date(matcher.group(1));
				//return df.parse(matcher.group(1));
			}

			matcher = CommonFun.GetMatcherStrGroup(content,patter);
			if(matcher.find())
			{
				String date = matcher.group(1);
				if(date.equals("1 Jan 1970"))
				{
					if(matcher.find())
						date = matcher.group(1);
				}
				return new Date(date);
			}
		} catch (Exception e) {
			logger.error(e.toString());
		}	
		return null;		
	}

	public static String getStartTime(String content)
	{
		//old before 2006.7.3
//		<td width=12% ALIGN=CENTER VALIGN=TOP class="H1">3:45</td>
//		<td width=12% ALIGN=CENTER VALIGN=TOP class="H1">4:10</td>
//		<td width="35" style="vertical-align:top;"><b>1:20</b><br>
//		<td width="35" style="vertical-align:top;"><b>4:20</b><br>
//		<td width="35" style="vertical-align:top;"><b>2:45</b><br>
//		<td width=12% ALIGN=CENTER VALIGN=TOP class="H1">1:50</td>

		//new 
//		<td width="35" style="vertical-align:top;" class="B1"><b>6:00</b></td>

//		<table width='100%' cellspacing=0 cellpadding=1 ALIGN=CENTER class="B2">
//		<tr>
//		<td width=12% align=center>[off 5:20]</td>
//		<td width=48%>&pound;3,812.41, &pound;888.28, &pound;391.72, &pound;226.21</td>
//		<td width=40% ALIGN=RIGHT class="B1"> &nbsp; <B><BIG>2m</BIG></B>&nbsp;</td>
//		</tr>
//		</table>

//		<table width='100%' cellspacing=0 cellpadding=1 ALIGN=CENTER>
//		<tr>
//		<td width=12% ALIGN=CENTER VALIGN=TOP class="H1">5:20</td>
//		<td width=88% class="H3">Winged Love (Pro/Am) INH Flat Race (Div II)<span class='B7'>&nbsp;(4yo+)</span></td>
//		</tr>
//		</table>

//		<table width='100%' cellspacing=0 cellpadding=1 ALIGN=CENTER class="B2">
//		<tr>
//		<td width=12% align=center>[off 5:20]</td>
//		<td width=48%>&pound;3,812.41, &pound;888.28, &pound;391.72, &pound;226.21</td>
//		<td width=40% ALIGN=RIGHT class="B1"> &nbsp; <B><BIG>2m</BIG></B>&nbsp;</td>
//		</tr>
//		</table>

//		<table width='595' border=0 cellspacing=0 cellpadding=0>
//		<tr>
//		<td width=55 VALIGN=TOP class="H1">5:20</td>
//		<td width=540 colspan=2 class="H3">Winged Love (Pro/Am) INH Flat Race (Div II)<span class='B7'>&nbsp;(4yo+)</span></td>
//		</tr>
//		</table>

//		</td>
//		</tr>
//		<tr><td>
//		<table width='595' border=0 cellspacing=0 cellpadding=0>
//		<tr class="B2" style="color:#808080;font-weight:bold;">
//		<td width=55 valign=top>[off 5:20]</td>
//		<td width=350 valign=top>&pound;3,812.41, &pound;888.28, &pound;391.72, &pound;226.21</td>

//		<td class="B1" align=right>&nbsp; <B><BIG>2m</BIG></B>&nbsp;&nbsp;</td>
//		</tr>
//		</table>

//		<table width='595' border=0 cellspacing=0 cellpadding=0>
//		<tr>
//		<td width=55 VALIGN=TOP class="H1">5:20</td>
//		<td width=540 colspan=2 class="H3">Winged Love (Pro/Am) INH Flat Race (Div II)<span class='B7'>&nbsp;(4yo+)</span></td>
//		</tr>
//		</table>
//		</td>
//		</tr>
//		<tr><td>
//		<table width='595' border=0 cellspacing=0 cellpadding=0>
//		<tr class="B2" style="color:#808080;font-weight:bold;">
//		<td width=55 valign=top>[off 5:20]</td>
//		<td width=350 valign=top>&pound;3,812.41, &pound;888.28, &pound;391.72, &pound;226.21</td>
//		<td class="B1" align=right>&nbsp; <B><BIG>2m</BIG></B>&nbsp;&nbsp;</td>
//		</tr>
//		</table>

//		<table width='770' border=0 cellspacing=0 cellpadding=0>
//		<tr>
//		<td width=55 VALIGN=TOP class="H1">1:00</td>

//		<td width=715 class="H3">wbx.com World Bet Exchange Maiden Claiming Stakes (Class 7)<span class='B7'>&nbsp;(3yo+)</span>
//		<span style="color:#808080;font-weight:bold;">&nbsp; <B><BIG>1m3f</span></td>
//		</tr>
//		</table>

		String sRaceTime = null;
		try {
			String strMatch = "<td width=55 VALIGN=TOP class=\"H1\">(\\d{1,2}):(\\d{2})</td>";
			Matcher matcher = CommonFun.GetMatcherStrGroup(content, strMatch);
			if (matcher.find()){			
				String sHH = matcher.group(1);
				String sMM = matcher.group(2);
				if (Integer.parseInt(sHH) < 11)
					sHH = String.valueOf(Integer.parseInt(sHH)+12);
				sRaceTime = sHH+":"+ sMM;
				return sRaceTime;
			}
//			String strMatch = "<b>(\\d{1,2}):(\\d{2})</b></td>";
			//<td width=55 VALIGN=TOP class="H1">2:30</td>
			strMatch = "<td width=55 VALIGN=TOP class=\"H1\">(\\d{1,2}):(\\d{2})</td>";
			matcher = CommonFun.GetMatcherStrGroup(content, strMatch);
			if (matcher.find()){			
				String sHH = matcher.group(1);
				String sMM = matcher.group(2);
				if (Integer.parseInt(sHH) < 11)
					sHH = String.valueOf(Integer.parseInt(sHH)+12);
				sRaceTime = sHH+":"+ sMM;
				return sRaceTime;
			}			
		} catch (Exception e) {
			logger.error(e.toString());
		}		
		return sRaceTime;	
	}
	
	public static String getStartTime(String content,boolean isFR)
	{
		//old before 2006.7.3
//		<td width=12% ALIGN=CENTER VALIGN=TOP class="H1">3:45</td>
//		<td width=12% ALIGN=CENTER VALIGN=TOP class="H1">4:10</td>
//		<td width="35" style="vertical-align:top;"><b>1:20</b><br>
//		<td width="35" style="vertical-align:top;"><b>4:20</b><br>
//		<td width="35" style="vertical-align:top;"><b>2:45</b><br>
//		<td width=12% ALIGN=CENTER VALIGN=TOP class="H1">1:50</td>

		//new 
//		<td width="35" style="vertical-align:top;" class="B1"><b>6:00</b></td>

//		<table width='100%' cellspacing=0 cellpadding=1 ALIGN=CENTER class="B2">
//		<tr>
//		<td width=12% align=center>[off 5:20]</td>
//		<td width=48%>&pound;3,812.41, &pound;888.28, &pound;391.72, &pound;226.21</td>
//		<td width=40% ALIGN=RIGHT class="B1"> &nbsp; <B><BIG>2m</BIG></B>&nbsp;</td>
//		</tr>
//		</table>

//		<table width='100%' cellspacing=0 cellpadding=1 ALIGN=CENTER>
//		<tr>
//		<td width=12% ALIGN=CENTER VALIGN=TOP class="H1">5:20</td>
//		<td width=88% class="H3">Winged Love (Pro/Am) INH Flat Race (Div II)<span class='B7'>&nbsp;(4yo+)</span></td>
//		</tr>
//		</table>

//		<table width='100%' cellspacing=0 cellpadding=1 ALIGN=CENTER class="B2">
//		<tr>
//		<td width=12% align=center>[off 5:20]</td>
//		<td width=48%>&pound;3,812.41, &pound;888.28, &pound;391.72, &pound;226.21</td>
//		<td width=40% ALIGN=RIGHT class="B1"> &nbsp; <B><BIG>2m</BIG></B>&nbsp;</td>
//		</tr>
//		</table>

//		<table width='595' border=0 cellspacing=0 cellpadding=0>
//		<tr>
//		<td width=55 VALIGN=TOP class="H1">5:20</td>
//		<td width=540 colspan=2 class="H3">Winged Love (Pro/Am) INH Flat Race (Div II)<span class='B7'>&nbsp;(4yo+)</span></td>
//		</tr>
//		</table>

//		</td>
//		</tr>
//		<tr><td>
//		<table width='595' border=0 cellspacing=0 cellpadding=0>
//		<tr class="B2" style="color:#808080;font-weight:bold;">
//		<td width=55 valign=top>[off 5:20]</td>
//		<td width=350 valign=top>&pound;3,812.41, &pound;888.28, &pound;391.72, &pound;226.21</td>

//		<td class="B1" align=right>&nbsp; <B><BIG>2m</BIG></B>&nbsp;&nbsp;</td>
//		</tr>
//		</table>

//		<table width='595' border=0 cellspacing=0 cellpadding=0>
//		<tr>
//		<td width=55 VALIGN=TOP class="H1">5:20</td>
//		<td width=540 colspan=2 class="H3">Winged Love (Pro/Am) INH Flat Race (Div II)<span class='B7'>&nbsp;(4yo+)</span></td>
//		</tr>
//		</table>
//		</td>
//		</tr>
//		<tr><td>
//		<table width='595' border=0 cellspacing=0 cellpadding=0>
//		<tr class="B2" style="color:#808080;font-weight:bold;">
//		<td width=55 valign=top>[off 5:20]</td>
//		<td width=350 valign=top>&pound;3,812.41, &pound;888.28, &pound;391.72, &pound;226.21</td>
//		<td class="B1" align=right>&nbsp; <B><BIG>2m</BIG></B>&nbsp;&nbsp;</td>
//		</tr>
//		</table>

//		<table width='770' border=0 cellspacing=0 cellpadding=0>
//		<tr>
//		<td width=55 VALIGN=TOP class="H1">1:00</td>

//		<td width=715 class="H3">wbx.com World Bet Exchange Maiden Claiming Stakes (Class 7)<span class='B7'>&nbsp;(3yo+)</span>
//		<span style="color:#808080;font-weight:bold;">&nbsp; <B><BIG>1m3f</span></td>
//		</tr>
//		</table>

		String sRaceTime = null;
		try {
			String strMatch = "<td width=55 VALIGN=TOP class=\"H1\">(\\d{1,2}):(\\d{2})</td>";
			Matcher matcher = CommonFun.GetMatcherStrGroup(content, strMatch);
			if (matcher.find()){			
				String sHH = matcher.group(1);
				String sMM = matcher.group(2);
				if(sHH.length()<2)
					sHH = "0"+sHH;
				sRaceTime = sHH+":"+ sMM;
				return sRaceTime;
			}
//			String strMatch = "<b>(\\d{1,2}):(\\d{2})</b></td>";
			//<td width=55 VALIGN=TOP class="H1">2:30</td>
			strMatch = "<td width=55 VALIGN=TOP class=\"H1\">(\\d{1,2}):(\\d{2})</td>";
			matcher = CommonFun.GetMatcherStrGroup(content, strMatch);
			if (matcher.find()){			
				String sHH = matcher.group(1);
				String sMM = matcher.group(2);
				if(sHH.length()<2)
					sHH = "0"+sHH;
				sRaceTime = sHH+":"+ sMM;
				return sRaceTime;
			}			
		} catch (Exception e) {
			logger.error(e.toString());
		}		
		return sRaceTime;	
	}

	public static String getStartTime1(String content)
	{
		//old before 2006.7.3
//		<td width=12% ALIGN=CENTER VALIGN=TOP class="H1">3:45</td>
//		<td width=12% ALIGN=CENTER VALIGN=TOP class="H1">4:10</td>
//		<td width="35" style="vertical-align:top;"><b>1:20</b><br>
//		<td width="35" style="vertical-align:top;"><b>4:20</b><br>
//		<td width="35" style="vertical-align:top;"><b>2:45</b><br>
//		<td width=12% ALIGN=CENTER VALIGN=TOP class="H1">1:50</td>

		//new 
//		<td width="35" style="vertical-align:top;" class="B1"><b>6:00</b></td>

		String sRaceTime = "00:00";
		try {
			String strMatch = "<td width=12% ALIGN=CENTER VALIGN=TOP class=\"H1\">(\\d{1,2}):(\\d{2})</td>";
			String strMatch1 = "<td width=\"35\" style=\"vertical-align:top;\"><b>(\\d{1,2}):(\\d{2})</b><br>";
			Matcher matcher = CommonFun.GetMatcherStrGroup(content, strMatch1);
			boolean isfind = matcher.find();
			if(!isfind)
			{
				matcher = CommonFun.GetMatcherStrGroup(content, strMatch);
				isfind = matcher.find();
			}
			if (isfind){			
				String sHH = matcher.group(1);
				String sMM = matcher.group(2);
				if (Integer.parseInt(sHH) < 11)
					sHH = String.valueOf(Integer.parseInt(sHH)+12);
				sRaceTime = sHH+":"+ sMM;
			}			
		} catch (Exception e) {
			logger.error(e.toString());
		}		
		return sRaceTime;	
	}

	public static String GetTypeAge(String title)
	{
		try {
			
//		(Class H)(Conditional Jockeys' And Amateur Riders' Race)(4-6yo)
//		(Class E)(4yo+,0-105)
//		(3yo+)
//		(2yo)
//		(Pro/Am)(5-7yo)
//		(3-4yo)
//		(4)(2yo,0-95)
//		(4-6yo,70-102)
//		(4yo+,70-109)	
//		Burlington Slate Cartmel Grand Veterans National Handicap Chase (Class D)(10yo+,0-125)
//		Burlington Slate Cartmel Grand Veterans National Handicap Chase (Class 3)(10yo+,0-125)

//		G.P.T. Waterford I.N.H. Flat Race(4yo+,--)
//		Railway Beginners Chase(4yo+,--)
		
//		<p><B>HAPPY BIRTHDAY YVONNE TURNER HANDICAP</B>(CLASS 5)(3yo+ 0-70) Winner &pound;2,914<strong> RUK </strong><br />
//		<strong>Good To Firm </strong><strong>7f100y</strong>
//		Number of runners: 13</p>
		 
		String ageName = null;
		String sql = "select Code from Code_Age where Meaning = '";
		
//		ageName = title; 
//		if(ageName.indexOf("10yo+")!=-1) {
//			ageName = "9yo+";
//		}
//		
//		if (ageName != null && !ageName.equals("")) {
//			return CommonBll.GetColumn(sql+ageName+"'");
//		}
//		
//		if (true) return CommonBll.GetColumn(sql+"Unrestricted'");
		
		
		//String patter = "\\((\\d{1,2}yo\\+?)(,?\\s?\\d{1,3}-\\d{1,3})?\\)";
		String patter = "\\((\\d{1,2}yo\\+?)";
		Matcher matcher = CommonFun.GetMatcherStrGroup(title,patter);
		if(matcher.find())
		{
			ageName = matcher.group(1);
			if(ageName.indexOf("10yo+")!=-1) {
				ageName = "9yo+";
				return CommonBll.GetColumn(sql+ageName+"'");
			}
				
			return CommonBll.GetColumn(sql+ageName+"'");
		}
		//patter = "\\((\\d)-(\\d)yo\\+?(,?\\s?\\d{1,3}-\\d{1,3})?\\)";
		patter = "\\((\\d)-(\\d)yo\\+?";
		matcher = CommonFun.GetMatcherStrGroup(title,patter);
		if(matcher.find())
		{
			int i = Integer.parseInt(matcher.group(1));
			int j = Integer.parseInt(matcher.group(2));
			if(j-i>1)
				return CommonBll.GetColumn(sql+i+"yo+'");
			else if(j-i==1)
				return CommonBll.GetColumn(sql+j+"yo'");
		}
//		G.P.T. Waterford I.N.H. Flat Race(4yo+,--)
//		Railway Beginners Chase(4yo+,--)
		//patter = "\\((\\d{1,2}yo\\+?),?\\s?--\\)";
		patter = "\\((\\d{1,2}yo\\+?)";
		matcher = CommonFun.GetMatcherStrGroup(title,patter);
		if(matcher.find())
			return CommonBll.GetColumn(sql+matcher.group(1)+"'");

//		Maloney & Rhodes Handicap(,0-90)
//		Bedford Lodge Hotel Handicap(,0-80)
		return CommonBll.GetColumn(sql+"Unrestricted'");
		
		} catch (Exception e) {
			logger.error("", e);
			return null;
		}
		
	}
	
	public static String GetLBW(String str) {
		Double lbw = null;
		str = str.replaceAll("¼", "&frac14").replaceAll("½", "&frac12").replaceAll("¾", "&frac34");
		if (str.indexOf("&frac") > -1) {
			if (str.indexOf("&frac") == 0) {
				double dStart = Double.parseDouble(str.substring(5,6));
				double dEnd = Double.parseDouble(str.substring(6,7));
				lbw = dStart / dEnd ;
			} else {
				int iFirst = Integer.parseInt(str.substring(0, str.indexOf("&frac")));
				double dStart = Double.parseDouble(str.substring(str.indexOf("&frac") + 5, str.indexOf("&frac") + 6));
				double dEnd = Double.parseDouble(str.substring(str.indexOf("&frac") + 6, str.indexOf("&frac") + 7));
				lbw = iFirst + dStart / dEnd;
			}
		} else {
			if (CommonFun.isNumber(str)) {
				lbw = Double.parseDouble(str);
			}
		}
		if (lbw == null) return null;
		return lbw+"";
	}
	
	public static String GetTypeBreed(String title)
	{
		String sql = "select Code from Code_TypeBreed where Meaning = '";
		if (title.indexOf("Colts & Geldings") != -1)
			return CommonBll.GetColumn(sql+"Colts & Geldings'");
		if (title.indexOf("Fillies & Mares") != -1)
			return CommonBll.GetColumn(sql+"Fillies & Mares'");
		if (title.indexOf("Colts & Fillies") != -1)
			return CommonBll.GetColumn(sql+"Colts & Fillies'");
			
		return CommonBll.GetColumn(sql+"Unrestricted'");
	}
	
	public static Byte GetAWT1(String title)
	{
		if (title.indexOf("(Dirt)") != -1)
			return new Byte("1");
		if (title.indexOf("(All Weather)") != -1)
			return new Byte("1");
		if (title.indexOf("(AWT)") != -1)
			return new Byte("1");
		return new Byte("0");
	}

	public static Byte GetAWT(String content)
	{
//		<div align=center class="H1">LINGFIELD (A.W) <span class="B1" align="center"><b>(06 Jan 2004)</b></span></div>
		//String patter = "\\(A.W\\)";
//		<span class="H24"><b>SOUTHWELL (A.W)</b></span><BR>

//		<td width="430" class="B11" valign=top align=center><font color=#333333>
//		<div class="H24"><b>WOLVERHAMPTON (A.W)</b></div>

//		20&nbsp; July&nbsp;2006    <BR>
//		STANDARD (The odd thundery shower is possible, but it will be mainly dry with sunny spells)    </font>
//		</td>

//		<tr>
//		<td class="B11" valign=top align=center><font color=#333333>
//		<span class="H24"><b>SOUTHWELL (A.W)</b></span><BR>
//		<span class="B11">02&nbsp; November&nbsp;2006</span><BR>

//		<span class="B12"><b>Standard</b></span>
//		</td>
//		</tr>

		String patter = "<div class=\"H24\">(.*?)</div>";
		Matcher matcher = CommonFun.GetMatcherStrGroup(content,patter);
		if(matcher.find()){
			RaceCommonFun o = new RaceCommonFun();
			String val = o.deleteTag(matcher.group(1).toLowerCase());
			if (val.indexOf("(a.w)")>-1)
				return new Byte("2");
		}
		patter = "<span class=\"H24\">(.*?)</span>";
		matcher = CommonFun.GetMatcherStrGroup(content,patter);
		if(matcher.find()){
			RaceCommonFun o = new RaceCommonFun();
			String val = o.deleteTag(matcher.group(1).toLowerCase());
			if (val.indexOf("(a.w)")>-1)
				return new Byte("2");
		}
		return new Byte("1");
	}

//	public static Byte GetGroupID(String title)
//	{
//		String patterGroupStr = "\\((Group.*?)\\)";
//		String patterGradeStr = "\\((Grade.*?)\\)";
//		String patterListedStr = "\\((Listed.*?)\\)";
//		Matcher myGroupMatcher = CommonFun.GetMatcherStrGroup(title, patterGroupStr);
//		Matcher myGradeMatcher = CommonFun.GetMatcherStrGroup(title, patterGradeStr);
//		Matcher myListedMatcher = CommonFun.GetMatcherStrGroup(title, patterListedStr);
//		if(myGroupMatcher.find())
//		{
//			Object obj = DBAccess.GetObjByValue(CodeGroup.class, "GroupName",myGroupMatcher.group(1).trim());
//			if(obj!=null)
//				return ((CodeGroup)obj).getGroupId();
//		}
//		else if(myGradeMatcher.find())
//		{
//			Object obj = DBAccess.GetObjByValue(CodeGroup.class, "GroupName",myGradeMatcher.group(1).trim());
//			if(obj!=null)
//				return ((CodeGroup)obj).getGroupId();
//		}
//		else if(myListedMatcher.find())
//		{
//			String listedStr = myListedMatcher.group(1);
//			Object obj = DBAccess.GetObjByValue(CodeGroup.class, "GroupName",listedStr);
//			if(obj!=null)
//				return ((CodeGroup)obj).getGroupId();
//			else
//			{
//				CodeGroup cg = new CodeGroup();
//				int maxid = 1;
//				obj = DBAccess.GetMaxIDObj(CodeGroup.class.getName(), "GroupId");
//				if(obj!=null)
//					maxid = ((CodeGroup)obj).getGroupId().intValue()+1;
//				cg.setGroupId(new Byte(maxid+""));
//				cg.setGroupName(listedStr);
//				return ((CodeGroup)DBAccess.create(cg)).getGroupId();
//			}
//		}
//		return null;
//	}
//
//	public static CodeClaimSeller GetCodeClaimSeller(String title) {
//		if (title.indexOf("Selling") != -1)
//			return (CodeClaimSeller)DBAccess.GetObjByValue(CodeClaimSeller.class, "ClaimSellerName", "Selling");
//		else if (title.indexOf("Claiming") != -1)
//			return (CodeClaimSeller)DBAccess.GetObjByValue(CodeClaimSeller.class, "ClaimSellerName", "Claiming");
//		else
//			return null;
//	}
//
//	public static CodeRider GetCodeRider(String title) {
//		if (title.indexOf("Amateur") != -1)
//			return (CodeRider)DBAccess.GetObjByValue(CodeRider.class, "RiderName","Amateur");
//		else if (title.indexOf("Apprentice") != -1)
//			return (CodeRider)DBAccess.GetObjByValue(CodeRider.class, "RiderName","Apprentice");
//		else if (title.indexOf("Ladies") != -1)
//			return (CodeRider)DBAccess.GetObjByValue(CodeRider.class, "RiderName","Ladies");
//		else
//			return null;
//	}

	public static String ReplaceName(String sName)
	{
		if(sName!=null)
			return sName.trim().replaceAll("&nbsp;"," ").replaceAll("&acute;","'").replaceAll("<strong class=\"uppercase\">", "");
		else
			return null;
	}

	public static Byte GetHandicap(String title) {
		title = title.toLowerCase();
		if (title.indexOf("handicap") != -1 || title.indexOf("hcap") != -1 || title.indexOf("h''cap") != -1 || title.indexOf("nursery") != -1)
			return new Byte("1");
		else
			return new Byte("0");
	}

	public static Byte GetMainden(String title) {
		if (title.indexOf("Maiden") != -1)
			return new Byte("1");
		else
			return new Byte("0");
	}

	public static Byte GetNovice(String title) {
		if (title.indexOf("Novice") != -1)
			return new Byte("1");
		else
			return new Byte("0");
	}

	public static Short GetHcapgrade(String content) {
//		(5yo+,0-109)
//		(,0-70)
//		(5yo+)
//		(4-7yo)
		String pattern = "\\(.*?,\\d-(\\d{0,3})\\)";
		Matcher matcher = CommonFun.GetMatcherStrGroup(content, pattern);
		if (matcher.find())
			return new Short(matcher.group(1));
		else
			return null;
	}

	public static Short GetMinHcap(String content) {
//		(5yo+,0-109)
//		(,0-70)
//		(5yo+)
//		(4-7yo)
//		<li>
//		 (Class 4) (0-100, 5yo+) (3m1f110y) 3m1&frac12;f Good 19 fences </li>
//		Oyster Partnership Handicap (Class 5) (0-70, 4yo+) 1m5f Standard
//		Ashford Environmental Handicap (Class 4) (0-85, 3yo) 1m2f Standard
//		Timeform Betfair Racing Club Standard Open National Hunt Flat Race (Class 6) (4-6yo) (2m2f110y) 2m2&frac12;f Good
//		Betfair Supports Oaksey House Handicap Hurdle (Class 5) (0-90, 4yo+) 3m3f Good 13 hdles
//		Betfair Funds PJA Doctor "Hands And Heels" Jumps Series Handicap Hurdle (Conditonals & Amateurs) (Class 5) (0-95, 4yo+) 2m4f Good 10 hdles
//		Betfair Sponsors Pride of Racing Dinner Handicap Chase (Class 5) (0-95, 5yo+) 2m4f Good 15 fences
//		Great Offers At wolverhampton-racecourse.co.uk Handicap (Class 7) (0-50, 4yo+) (1m141y) 1m&frac12;f Standard
//		ladbrokespoker.com Handicap (Class 3) (0-95, 4yo+) (7f32y) 7f Standard

		String pattern = "\\(.*?,?\\s?(\\d{1,2})-(\\d{1,3})\\)";
		Matcher matcher = CommonFun.GetMatcherStrGroup(content, pattern);
		if (matcher.find())
			return new Short(matcher.group(1));
		
		pattern = "\\((\\d{1,2})-\\d{1,3}, \\dyo\\+?\\)";
		matcher = CommonFun.GetMatcherStrGroup(content, pattern);
		if (matcher.find())
			return new Short(matcher.group(1));
		
		return null;
	}

	public static Short GetMaxHcap(String content) {
//		(5yo+,0-109)
//		(,0-70)
//		(5yo+)
//		(4-7yo)
		String pattern = "\\(.*?,?\\s?\\d{1,2}-(\\d{1,3})\\)";
		Matcher matcher = CommonFun.GetMatcherStrGroup(content, pattern);
		if (matcher.find())
			return new Short(matcher.group(1));
		
		pattern = "\\(\\d{1,2}-(\\d{1,3}), \\dyo\\+?\\)";
		matcher = CommonFun.GetMatcherStrGroup(content, pattern);
		if (matcher.find())
			return new Short(matcher.group(1));
		
		return null;
	}

	public static String GetTitle(String strHtml) {
		String strPattern = "<td width=88% class=\"H3\">(.*?)<span";
		Matcher matcher = CommonFun.GetMatcherStrGroup(strHtml, strPattern);
		if (matcher.find())
			return matcher.group(1).trim();
		else
			return null;
	}

//	public static CodeTrack getTrack(String raceTrackName,String raceAreaName)
//	{
//		try
//		{
//			raceTrackName = RaceCommonFun.ReplaceName(raceTrackName);
//			Object obj = DBAccess.GetObjByValue(CodeTrack.class,"TrackName",raceTrackName);
//			if(obj!=null)
//			{
//				CodeTrack ct = (CodeTrack)obj;
//				if(ct.getCodeRaceArea().getRaceAreaId()==null)
//				{
//					logger.error("trackname="+ct.getTrackName()+" raceareaid is null");
//					return null;
//				}
//				int raceareaid = ct.getCodeRaceArea().getRaceAreaId().intValue();
//				if(raceareaid==1||raceareaid==43||raceareaid==6)
//					return (CodeTrack)obj;
//			}
//			else
//			{
//				String trackName = new ZTStd().getColValue("code_trackmapping", "TrackName", " where MappingName = '"+raceTrackName+"'");
//				if(trackName.length()>0)
//				{
//					obj = DBAccess.GetObjByValue(CodeTrack.class,"TrackName",trackName);
//					if(obj!=null)
//					{
//						CodeTrack ct = (CodeTrack)obj;
//						if(ct.getCodeRaceArea().getRaceAreaId()==null)
//						{
//							logger.error("trackname="+ct.getTrackName()+" raceareaid is null");
//							return null;
//						}
//						int raceareaid = ct.getCodeRaceArea().getRaceAreaId().intValue();
//						if(raceareaid==1||raceareaid==43||raceareaid==6)
//							return (CodeTrack)obj;
//					
//					}
//				}
//				else
//				{
//					CodeTrack ct = new CodeTrack();
//					ct.setTrackName(raceTrackName);
//	//				Object obj = DBAccess.GetObjByID(PreRaceRace.class,new Integer(raceid),1);				
//					CodeRaceArea cra = null;
//					
//					if(raceAreaName!=null&&raceAreaName.toLowerCase().equals("fr"))
//						cra = (CodeRaceArea)DBAccess.GetObjByID(CodeRaceArea.class, new Byte("6"), 2);
//					else if(raceAreaName!=null&&raceAreaName.toLowerCase().equals("ire"))
//						cra = (CodeRaceArea)DBAccess.GetObjByID(CodeRaceArea.class, new Byte("1"), 2);
//					else
//						cra = (CodeRaceArea)DBAccess.GetObjByID(CodeRaceArea.class, new Byte("43"), 2);
//					
//					ct.setCodeRaceArea(cra);
//					obj = DBAccess.GetMaxIDObj(CodeTrack.class.getName(),"TrackId");
//					if(obj!=null)
//						ct.setTrackId(new Short(((CodeTrack)obj).getTrackId().intValue()+1+""));
//					else
//						ct.setTrackId(new Short("1"));
//					return (CodeTrack)DBAccess.create(ct);
//				}
//			}
//		}
//		catch(Exception e)
//		{
//			logger.error(e);
//		}
//		return null;
//	}
//
//	public static CodeTrack getTrack(String raceTrackName,CodeRaceArea cra)
//	{
//		raceTrackName = RaceCommonFun.ReplaceName(raceTrackName);
//		Object obj = DBAccess.GetObjByValue(CodeTrack.class,"TrackName",raceTrackName);
//		if(obj!=null)
//		{
//			CodeTrack ct = (CodeTrack)obj;
//			if(ct.getCodeRaceArea()!=null||cra==null)
//				return (CodeTrack)obj;
//			else
//			{
//				ct.setCodeRaceArea(cra);
//				return (CodeTrack)DBAccess.save(ct);
//			}
//		}
//		else
//		{
//			CodeTrack ct = new CodeTrack();
//			ct.setTrackName(raceTrackName);
//			ct.setCodeRaceArea(cra);
//			obj = DBAccess.GetMaxIDObj(CodeTrack.class.getName(),"TrackId");
//			if(obj!=null)
//				ct.setTrackId(new Short(((CodeTrack)obj).getTrackId().intValue()+1+""));
//			else
//				ct.setTrackId(new Short("1"));
//			return (CodeTrack)DBAccess.create(ct);
//		}		
//	}

	public static String GetTitle1Title2(String sBody,int index) {
		String sPattern = "<table width='98%' cellspacing=0 cellpadding=1 ALIGN=CENTER class=\"table1\">";
		sPattern += ".*?<table.*?>(.*?)</table>.*?<table.*?>(.*?)</table>";
		Matcher matcher = CommonFun.GetMatcherStrGroup(sBody,sPattern);
		if (matcher.find()) {
			String Title1 = matcher.group(1);
			String Title2 = matcher.group(2);
			if(index==1)
				return Title1;
			else
				return Title2;
		}
		return null;
	}

//	public static boolean Save(Object object)
//	{
//	boolean isSucc = false;
//	try {
//	DBAccess.create(object);
//	isSucc = true;
//	} catch (Exception e) {
//	logger.error(e);
//	}
//	return isSucc;
//	}

	public static BigDecimal GetRacePrize(String strHtml) {
		String strPattern = "<span class=\"b5\"><i>Winner &pound;(.*?)</i>";
		Matcher matcher = CommonFun.GetMatcherStrGroup(strHtml, strPattern);
		if (matcher.find()) {
			String money = matcher.group(1).replaceAll(",", "");
			if (!money.equals(""))
				return new BigDecimal(money);
			else
				return null;
		} else
			return null;
	}

	public static String GetYYYYMMDDFromYYYY_MM_DD(String mydate) {
		String strPattern = "(\\d{4})-(\\d{1,2})-(\\d{1,2})";
		Matcher matcher = CommonFun.GetMatcherStrGroup(mydate, strPattern);
		if (matcher.find())
		{
			String yyyy = matcher.group(1);
			String mm = matcher.group(2);
			if(mm.length()<2)mm = "0"+mm;
			String dd = matcher.group(3);
			if(dd.length()<2)dd = "0"+dd;
			return yyyy+mm+dd;
		}
		return null;
	}

	public static String GetYYYY_MM_DDFromYYYYMMDD(String mydate) {
		String strPattern = "(\\d{4})(\\d{2})(\\d{2})";
		Matcher matcher = CommonFun.GetMatcherStrGroup(mydate, strPattern);
		if (matcher.find())
		{
			String yyyy = matcher.group(1);
			String mm = matcher.group(2);
			String dd = matcher.group(3);
			return yyyy+"-"+mm+"-"+dd;
		}
		return null;
	}

	public static java.util.Date GetRaceDateTime(String strHtml,String racedate) {
		DateFormat mydf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date raceDate = new Date();
		try {
			String raceTimeStrPattern = "<b>(\\d{1,2}):(\\d{2})</b><br>";
			Matcher matcher = CommonFun.GetMatcherStrGroup(strHtml,
					raceTimeStrPattern);
			if (matcher.find()) {
				String raceTimeHour = matcher.group(1).trim();
				String raceTimeMinute = matcher.group(2).trim();
				if (Integer.parseInt(raceTimeHour) < 11)
					raceDate = mydf.parse(racedate + " "
							+ (Integer.parseInt(raceTimeHour) + 12) + ":"
							+ raceTimeMinute + ":00");
				else
					raceDate = mydf.parse(racedate + " " + raceTimeHour
							+ ":" + raceTimeMinute + ":00");
			} else
				raceDate = null;
		} catch (Exception e) {
			logger.error("GetRaceDateTime exception :" + e.toString());
		}
		logger.info("time is : " + raceDate.toLocaleString());
		return raceDate;
	}

//	public static Byte GetDivisionID(String title)
//	{
//		String patterStr = "\\((Div.*?)\\)";
//		String divStr = null;
//		Matcher myMatcher = CommonFun.GetMatcherStrGroup(title, patterStr);
//		if(myMatcher.find())
//		{
//			divStr = myMatcher.group(1);
//			Object obj = (CodeDivision)DBAccess.GetObjByValue(CodeDivision.class,"DivisionName", divStr);
//			if(obj!=null)
//				return ((CodeDivision)obj).getDivisionId();
//		}
//		return null;
//	}
//
//	public static CodeRaceArea GetCodeRaceArea(CodeTrack ct)
//	{
//		List list = DBAccess.GetObjListByHql("select a.CodeRaceArea from CodeTrack a where a.TrackId ="+ct.getTrackId().toString());
//		Iterator it = list.iterator();
//		if(it.hasNext())
//			return (CodeRaceArea)it.next();
//		return null;
//	}
//
//	public static Byte GetNewClassID(String title)
//	{
//		String patterStr = "\\(([A-Z]\\d)\\)";
//		Matcher myMatcher = CommonFun.GetMatcherStrGroup(title, patterStr);
//		if(myMatcher.find())
//			return RaceCommonFun.GetNewClassIDFromName(myMatcher.group(1)).getNewClassId();
//		return null;
//	}
//
//	public static CodeNewClass GetNewClassIDFromName(String classname)
//	{
//		Object obj = DBAccess.GetObjByValue(CodeNewClass.class, "NewClassName",classname);
//		if(obj!=null)
//			return (CodeNewClass)obj;
//		else
//		{
//			CodeNewClass cc = new CodeNewClass();
//			int maxid = 1;
//			obj = DBAccess.GetMaxIDObj(CodeNewClass.class.getName(), "NewClassId");
//			if(obj!=null)
//				maxid = ((CodeNewClass)obj).getNewClassId().intValue()+1;
//			cc.setNewClassId(new Byte(maxid+""));
//			cc.setNewClassName(classname);
//			return (CodeNewClass)DBAccess.create(cc);
//		}
//	}

	public static boolean UpdateDraw(String raceid,String horseid,String draw)
	{
		ZTStd myztstd = new ZTStd();
		String updateSql = "update prerace_horse set draw = "+draw+" where raceid ="+raceid+" and horseid ="+horseid;
		int resultNum = 0;
		try {		
			logger.info(updateSql);
			resultNum = myztstd.getResultByUpdate(updateSql);
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return resultNum == 1 ? true : false;	
	}

//	public static CodeAgeLimit GetAgeLimit(String strHtml) {
//		String sAgeLimit = null;
//		String strPattern = "\\((\\d([-]\\d)?yo\\+?).*?\\)";
//		Matcher matcher = CommonFun.GetMatcherStrGroup(strHtml, strPattern);
//		if (matcher.find()) {
//			String ageStr = matcher.group(1);
//			sAgeLimit = ageStr;
//			if (sAgeLimit!=null && !sAgeLimit.equals("")){
//				if (RaceCommonFun.IsExistCodeAgeLimitMapping(sAgeLimit))
//					return RaceCommonFun.GetCodeAgeLimitMapping(sAgeLimit);
//				else
//					return RaceCommonFun.GetCodeAgeLimit(sAgeLimit);
//			}	
//		}
//		return null;
//	}
//
//	private static CodeAgeLimit GetCodeAgeLimit(String sValue)
//	{
//		Object obj = DBAccess.GetObjByValue(CodeAgeLimit.class,"AgeLimitName", sValue);
//		if(obj!=null)
//			return (CodeAgeLimit)obj;
//		else
//		{
//			CodeAgeLimit cbd = new CodeAgeLimit();
//			cbd.setAgeLimitName(sValue);
//			obj = DBAccess.GetMaxIDObj(CodeAgeLimit.class.getName(),"AgeLimitId");
//			if(obj!=null)
//				cbd.setAgeLimitId(new Byte(((CodeAgeLimit)obj).getAgeLimitId().intValue()+1+""));
//			else
//				cbd.setAgeLimitId(new Byte("1"));
//			return (CodeAgeLimit)DBAccess.create(cbd);						
//		}
//	}
//
//	private static boolean IsExistCodeAgeLimitMapping(String sValue)
//	{
//		try {
//			Object obj = DBAccess.GetObjByValue(CodeAgeLimitMapping.class,"MappingName",sValue);
//			if(obj!=null)
//				return true;
//		} catch (Exception e) {
//			CommonFun.logger.error(e.toString());
//		}
//		return false;
//	}
//
//	public static CodeAgeLimit GetCodeAgeLimitMapping(String sValue){
////		sValue = sValue.replaceAll("'","''");
//		try {
//			Object obj = DBAccess.GetObjByValue(CodeAgeLimitMapping.class,"MappingName",sValue);
//			if(obj!=null)
//			{
//				String Comment = ((CodeAgeLimitMapping)obj).getComment();
//				obj = DBAccess.GetObjByValue(CodeAgeLimit.class,"AgeLimitName",Comment);
//				if(obj!=null)
//					return (CodeAgeLimit)obj;
//			}
//		} catch (Exception e) {
//			CommonFun.logger.error(e.toString());
//		}
//		return null;
//	}

	public static Byte GetNoOfHorse(String strHtml) {
		int iNOhorse = 0;
		String strMatch = "<table.*?>.*?Comments In Running(.*?)</table>";
		Matcher matcher = CommonFun.GetMatcherStrGroup(strHtml, strMatch);
		if (matcher.find()) {
			String strTR = "<tr.*?>(.*?)</tr>";
			Matcher trMatcher = CommonFun.GetMatcherStrGroup(matcher.group(1).trim(), strTR);
			while (trMatcher.find()) {
				iNOhorse++;
			}
		}	
		return new Byte(iNOhorse+"");
	}

	public static Vector GetHistoryDate(int fYear, int fMonth, int fDay,
			int tYear, int tMonth, int tDay) {
		Vector vHistroyDate = new Vector();
		int Year;
		int DAY;
		int Month;
		String DateTime;
		int[] aMonth = { Calendar.JANUARY, Calendar.FEBRUARY, Calendar.MARCH,
				Calendar.APRIL, Calendar.MAY, Calendar.JUNE, Calendar.JULY,
				Calendar.AUGUST, Calendar.SEPTEMBER, Calendar.OCTOBER,
				Calendar.NOVEMBER, Calendar.DECEMBER };

		if (fMonth <= 0 || fMonth > 12) {
			System.out.println("input date error and exit programe");
		}
		GregorianCalendar gcFrom = new GregorianCalendar(fYear,
				aMonth[fMonth - 1], fDay);
		GregorianCalendar gcTo = new GregorianCalendar(tYear,
				aMonth[tMonth - 1], tDay);
		int tyear = gcTo.get(Calendar.YEAR);
		int tmonth = gcTo.get(Calendar.MONTH) + 1;
		int tday = gcTo.get(Calendar.DAY_OF_MONTH);
		String toDateTime = tyear + "-" + tmonth + "-" + tday;
		do {
			Year = gcFrom.get(Calendar.YEAR);
			Month = gcFrom.get(Calendar.MONTH) + 1;
			DAY = gcFrom.get(Calendar.DAY_OF_MONTH);
			DateTime = Year + "-" + Month + "-" + DAY;
			vHistroyDate.add(DateTime);
			gcFrom.add(Calendar.DAY_OF_YEAR, 1);
		} while (!DateTime.equals(toDateTime));
		return vHistroyDate;
	}

//	public static CodeBeatenDistance GetBeatenDistanceIDbyName(String name){
//		try {
//			Object obj = DBAccess.GetObjByValue(CodeBeatenDistance.class,"BeatenDistanceName", name);
//			if(obj!=null)
//				return (CodeBeatenDistance)obj;
//			else
//			{
//				CodeBeatenDistance cbd = new CodeBeatenDistance();
//				cbd.setBeatenDistanceName(name);
//				obj = DBAccess.GetMaxIDObj(CodeBeatenDistance.class.getName(),"BeatenDistanceId");
//				if(obj!=null)
//					cbd.setBeatenDistanceId(new Short(((CodeBeatenDistance)obj).getBeatenDistanceId().intValue()+1+""));
//				else
//					cbd.setBeatenDistanceId(new Short("1"));
//				return (CodeBeatenDistance)DBAccess.create(cbd);						
//			}
//		} catch (Exception ex) {
//			logger.error(ex.toString());
//		}
//		return null;
//	}
//
//	public static CodeBeatenDistanceNew GetBeatenDistanceValuebyName(String name){
//		try {
//			Object obj = DBAccess.GetObjByValue(CodeBeatenDistanceNew.class,"BeatendistanceName", name);
//			if(obj!=null)
//				return (CodeBeatenDistanceNew)obj;
//			else
//			{
//				CodeBeatenDistanceNew cbd = new CodeBeatenDistanceNew();
//				cbd.setBeatendistanceName(name);
//				obj = DBAccess.GetMaxIDObj(CodeBeatenDistanceNew.class.getName(),"BeatendistanceId");
//				if(obj!=null)
//					cbd.setBeatendistanceId(new Byte(((CodeBeatenDistanceNew)obj).getBeatendistanceId().intValue()+1+""));
//				else
//					cbd.setBeatendistanceId(new Byte("1"));
//				return (CodeBeatenDistanceNew)DBAccess.create(cbd);						
//			}
//		} catch (Exception ex) {
//			logger.error(ex.toString());
//		}
//		return null;
//	}
//

	public static Date GetYYYYMMDD(Date date)
	{
		return new Date(date.getYear(),date.getMonth(),date.getDate());
	}

	public static String GetHHMM(Date date)
	{
		if(date==null)
			return null;
		DateFormat df = new SimpleDateFormat("HH:mm");
		return df.format(date);
	}

	public static Short ParseDistance(String distanceStr)
	{
		if (distanceStr == null || distanceStr.trim().equals("")) return null;
		double distance = 0;
		double f = 0;
		char c;
		String field = "";
		char[] disArray = distanceStr.toCharArray();
		for(int i=0;i<disArray.length;i++)
		{
			c = disArray[i];
			if(c>='0'&&c<='9') {
				field +=c;
			}else if (c == '¼' || c == '½' || c == '¾') {
				String v = "";
				if (c == '¼') v = ".25";
				if (c == '½') v = ".5";
				if (c == '¾') v = ".75";
				field +=v;
			}else {
				f = Double.parseDouble(field);
				field = "";
				switch(c)
				{
				case 'm':
					distance += f * 1760;
					break;
				case 'f':
					distance += f * 220;
					break;
				case 'y':
					distance += f;
					break;
				}
			}			
		}
//		System.err.println(distance);
		return new Short((distance+"").replaceAll("\\.0", ""));
	}


//	public static Long GetURID(PreRaceRace myprr) {
//		try {
//			DateFormat mydf = new SimpleDateFormat("yyyyMMdd");
//			String timeStr = mydf.format(myprr.getRaceDate());
//			String trackID = null;
//			if(myprr.getCodeTrack()==null||myprr.getRaceNo()==null)
//				return new Long(myprr.getRaceId().toString());
//			trackID = myprr.getCodeTrack().getTrackId().intValue() + "";
//			while (trackID.length() < 3)
//				trackID = "0" + trackID;
//			String raceNo = myprr.getRaceNo().intValue() + "";
//			if (raceNo.length() < 2)
//				raceNo = "0" + raceNo;
//			String raceAreaCode = "99";
//			int areacode = 0;
//			if(myprr.getCodeRaceArea()!=null)
//				areacode = myprr.getCodeRaceArea().getRaceAreaId().intValue();
//			else
//				areacode = myprr.getCodeTrack().getCodeRaceArea().getRaceAreaId().intValue();
//			if (areacode == 43)
//				raceAreaCode = "03";
//			else if (areacode == 1)
//				raceAreaCode = "28";
//			else if (areacode == 6)
//				raceAreaCode = "05";
//			return new Long(timeStr + raceAreaCode + trackID + raceNo);
//		} catch (Exception e) {
//			logger.error(e);			
//		}
//		return new Long(myprr.getRaceId().toString());
//	}
//
//	public static Long GetURID(PostRaceRace myprr) {
//		try {
//			DateFormat mydf = new SimpleDateFormat("yyyyMMdd");
//			String timeStr = mydf.format(myprr.getRaceDate());
//			String trackID = null;
//			if(myprr.getCodeTrack()==null||myprr.getRaceNo()==null)
//				return new Long(myprr.getRaceId().toString());
//			trackID = myprr.getCodeTrack().getTrackId().intValue() + "";
//			while (trackID.length() < 3)
//				trackID = "0" + trackID;
//			String raceNo = myprr.getRaceNo().intValue() + "";
//			if (raceNo.length() < 2)
//				raceNo = "0" + raceNo;
//			String raceAreaCode = "99";
//			int areacode = 0;
//			if(myprr.getCodeRaceArea()!=null)
//				areacode = myprr.getCodeRaceArea().getRaceAreaId().intValue();
//			else
//				areacode = myprr.getCodeTrack().getCodeRaceArea().getRaceAreaId().intValue();
//			if (areacode == 43)
//				raceAreaCode = "03";
//			else if (areacode == 1)
//				raceAreaCode = "28";
//			else if (areacode == 6)
//				raceAreaCode = "05";
//			return new Long(timeStr + raceAreaCode + trackID + raceNo);
//		} catch (Exception e) {
//			logger.error(e);			
//		}
//		return new Long(myprr.getRaceId().toString());
//	}

	public static String GetFileName(String raceid,String yyyymmdd,String filedir)
	{
		File myFileName = new File(filedir+CommonFun.SYS_SEPARATOR+yyyymmdd+"_"+raceid+".html");
		if(!myFileName.exists())
		{	
			myFileName = new File(filedir+CommonFun.SYS_SEPARATOR+raceid+"_"+yyyymmdd+".html");
			if(!myFileName.exists())
			{
				myFileName = new File(filedir+CommonFun.SYS_SEPARATOR+raceid+".html");

				if(!myFileName.exists())
				{
					logger.warn("raceid is "+raceid+"'s file no find in "+filedir);
					return null;
				}
			}
		}
		return myFileName.toString();
	}

	public static String GetFileName1(String raceid,String yyyymmdd,String filedir1,String filedir2)
	{
		File myFileName = null;
		if(yyyymmdd==null)		
		{
			myFileName = new File(filedir1+CommonFun.SYS_SEPARATOR+raceid+".html");
			if(myFileName.exists())
				return myFileName.toString();
			else
			{
				logger.warn("raceid is "+raceid+"'s file no find in "+filedir1);
				return null;
			}				
		}
		myFileName = new File(filedir1+CommonFun.SYS_SEPARATOR+raceid+"_"+yyyymmdd+".html");
		if(!myFileName.exists())
		{
			myFileName = new File(filedir1+CommonFun.SYS_SEPARATOR+yyyymmdd+"_"+raceid+".html");
			if(!myFileName.exists())
			{
				myFileName = new File(filedir1+CommonFun.SYS_SEPARATOR+raceid+".html");
				if(!myFileName.exists())
				{
					logger.warn("raceid is "+raceid+"'s file no find in "+filedir1);
					return null;
				}

			}
		}
		return myFileName.toString();
	}

	public static Byte getDraw(String content)
	{
//		<span class="draw">8</span>
		String patter = "<span class=\"draw\">(\\d{1,2})</span>";
		Matcher matcher = CommonFun.GetMatcherStrGroup(content,patter);
		if(matcher.find())
			return new Byte(matcher.group(1));
		else
			return null;
	}

	public static Byte getHorseAge(String content)
	{
//		<td class="black">3</td>
		String patter = "\\d{1,2}";
		Matcher matcher = CommonFun.GetMatcherStrGroup(content,patter);
		if(matcher.find())
			return new Byte(matcher.group());
		else
			return null;
	}

	public static BigDecimal GetJockeyOverWeight(String sIn) {
		int dHcapWeight = 0;
		sIn += "<IMG";
		try {
			String strMatch = "IMG SRC=\"/images/furniture/oh.gif\".*?>(.*?)<";
			Matcher matcher = CommonFun.GetMatcherStrGroup(sIn, strMatch);
			while (matcher.find()) {
				if (CommonFun.isNumber(matcher.group(1).trim()))
					dHcapWeight = Integer.parseInt(matcher.group(1).trim());
			}
		} catch (Exception e) {
			logger.error(e.toString());
		}
		return new BigDecimal(dHcapWeight);
	}

	public static String getHorseId(String content)
	{
		String patter = "horse_id=(\\d{1,9})";
		Matcher matcher = CommonFun.GetMatcherStrGroup(content,patter);
		if(matcher.find())
			return matcher.group(1);
		else
			return null;
	}

//	public static CodeRaceArea getRaceArea(String raceAreaName)
//	{
//		raceAreaName = raceAreaName.trim().replaceAll("'","''").replaceAll("&nbsp;","").replaceAll("&acute;","");
//		Object obj = DBAccess.GetObjByValue(CodeRaceArea.class,"RaceAreaName",raceAreaName);
//		if(obj!=null)
//			return (CodeRaceArea)obj;
//		else
//		{
//			CodeRaceArea cra = new CodeRaceArea();
//			cra.setRaceAreaName(raceAreaName);
//			obj = DBAccess.GetMaxIDObj(CodeRaceArea.class.getName(),"RaceAreaId");
//			if(obj!=null)
//				cra.setRaceAreaId(new Byte(((CodeRaceArea)obj).getRaceAreaId().intValue()+1+""));
//			else
//				cra.setRaceAreaId(new Byte("1"));
//			return (CodeRaceArea)DBAccess.create(cra);
//		}		
//	}

	public static Date GetRaceDateFromUrl_yyyy_M_d(String myUrl) {
		try
		{
			String strPattern = "\\d{4}-\\d{1,2}-\\d{1,2}";
			DateFormat mydf = new SimpleDateFormat("yyyy-M-d");
			Matcher matcher = CommonFun.GetMatcherStrGroup(myUrl, strPattern);
			if (matcher.find())
				return mydf.parse(matcher.group(0).trim());
		}
		catch(Exception e)
		{
			logger.error(e);
		}
		return null;
	}

	public String deleteTag(String sOld){
		String sNew = sOld;
		sNew = replaceSymbol(sOld, "<.*?>", "");
		return sNew;
	}

	public String replaceSymbol(String sIn, String sSyb, String sReplace) {
		return sIn.replaceAll(sSyb, sReplace).trim();
//		jregex.Pattern p = new jregex.Pattern(sSyb);
//		Replacer r = p.replacer(sReplace);
//		sIn = r.replace(sIn);
//		return sIn.trim();
	}

	public static void main(String[] args) {
		RaceCommonFun o = new RaceCommonFun();
//		o.GetAWT("nnnnn<span class=\"H24\"><b>SOUTHWELL (A.W)</b></span><BR>dfgdfgdfg");
//		String name = "tb1";
//		if (name.endsWith("1")) name = name.substring(0, name.length()-1);
		System.out.println(ParseDistance("5f"));
//		System.out.println(ParseDistance("1m7½f"));
		System.out.println(ParseDistance("5½f"));
//		System.out.println(GetLBW("6¼"));
	}
}