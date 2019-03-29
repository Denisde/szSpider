package com.datalabchina.bll;

import java.io.File;
import java.util.List;
import java.util.regex.Matcher;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import com.datalabchina.controler.Controller;
import com.datalabchina.common.CommonDB;
import com.datalabchina.common.CommonMethod;
import com.datalabchina.common.FileDispose;


public class ParseRunners06Bll {
	private  static Logger logger = Logger.getLogger(ParseRaceInformation04Bll.class.getName());
	private  static CommonDB oCommonDB =new CommonDB();
	private  static CommonMethod oCommonMethod = new CommonMethod();	

	

	public  static void parseFile(String fileName){
//		fileName ="C:\\Users\\zhida.li\\AppData\\Roaming\\Skype\\My Skype Received Files\\XML\\20160815\\XML\\06_PARTANTS\\77573681.xml";
		String  body = FileDispose.readFile(fileName);
		/*, , , , , , ,
		 *  , , id_nav_reunion, num_externe_reunion, num_reunion, num_course_mere, distance_course, unite_dist_course,
		 *   total_allocations, nb_partants, discipline, num_course_pmu, libcourt_prix_course, liblong_prix_course, id_nav_course, conditions_txt_course,
		 *    race_cond_course, age_cond_course, sexe_cond_course, monte_cond_course, id_nav_partant, num_partant, nom_cheval, suffixe_cheval,
		 *     sexe_partant, joker_jockey, age_partant, race_partant, robe_partant, date_naiss_partant, place_corde_partant, corde_demandee,
		 *      oeil_partant, taux_reclam_partant, couleur_partant, echarpe_partant, ecurie_part, dist_partant, pds_calc_hand_partant, pds_cond_monte_partant,
		 *       surcharge_partant, eng_part_partant, deferrer_partant, ind_jum_pleine_partant, ind_inedit_partant, annee_cast_partant, statut_part, 
		 *       statut_part_pcc, type_eng, coul_casaque_partant, disp_casaque_partant, deux_coul_casaque_partant, disp_manche_partant, coul_manche_partant, 
		 *       deux_coul_manche_partant, coul_toque_partant, disp_toque_partant, deux_coul_toque_partant, nom_pere, suffixe_pere, race_pere, annee_naiss_pere,
		 *        nom_mere, suffixe_mere, race_mere, annee_naiss_mere, nom_pere_mere, suffixe_pere_mere, race_pere_mere, annee_naiss_pere_mere, lieu_entrainement,
		 *         code_entraineur, pays_entrainement, departement_entrainement, titre_entraineur, initiales_entraineur, prenom_entraineur, nom_entraineur, 
		 *         initiales_monte, code_monte, prenom_monte, titre_monte, nom_monte, prenom_proprietaire, code_proprietaire, nom_proprietaire, titre_proprietaire,
		 *          initiales_proprietaire, couleurs_proprietaire, nom_eleveur, ExtractTime
		 * */
		logger.info("Parse FileName ************************" +fileName+"***************************************");
		try {
			Matcher JoursMatcher = oCommonMethod.getMatcherStrGroup(body, "<jour>(.*?)</jour>");
		while(JoursMatcher.find()){
			String oneJours = JoursMatcher.group(1);
			String code_statut_infos = oCommonMethod.getValueByPatter(oneJours, "<code_statut_infos>(.*?)</code_statut_infos>");
			
			String libelle_statut_infos = oCommonMethod.getValueByPatter(oneJours, "<libelle_statut_infos translate=\"mandatory\"\\s?>(.*?)</libelle_statut_infos>");
			
			
			String date_heure_generation = oCommonMethod.getValueByPatter(oneJours, "<date_heure_generation>(.*?)</date_heure_generation>");
			
			String type_document = oCommonMethod.getValueByPatter(oneJours, "<type_document>(.*?)</type_document>");
			
			String date_jour_reunion = oCommonMethod.getValueByPatter(oneJours, "<date_jour_reunion>(.*?)</date_jour_reunion>");
			
			
			Matcher reunionMatcher = oCommonMethod.getMatcherStrGroup(oneJours, "<reunion>(.*?)</reunion>");
			while(reunionMatcher.find()){
				String oneReunion = reunionMatcher.group(1);
				String  libelle_hippo= oCommonMethod.getValueByPatter(oneReunion, "<libelle_hippo>(.*?)</libelle_hippo>");
				String  code_hippo= oCommonMethod.getValueByPatter(oneReunion, "<code_hippo>(.*?)</code_hippo>");
				String  type_reunion= oCommonMethod.getValueByPatter(oneReunion, "<type_reunion>(.*?)</type_reunion>");
				String  date_reunion= oCommonMethod.getValueByPatter(oneReunion, "<date_reunion>(.*?)</date_reunion>");
				String  id_nav_reunion= oCommonMethod.getValueByPatter(oneReunion, "<id_nav_reunion>(.*?)</id_nav_reunion>");
				String  num_externe_reunion= oCommonMethod.getValueByPatter(oneReunion, "<num_externe_reunion>(.*?)</num_externe_reunion>");
				String  num_reunion= oCommonMethod.getValueByPatter(oneReunion, "<num_reunion>(.*?)</num_reunion>");
				
				Matcher courses = oCommonMethod.getMatcherStrGroup(oneReunion, "<course>(.*?)</course>");
				while(courses.find()){
					String course = courses.group(1);
					String num_course_mere = oCommonMethod.getValueByPatter(course, "<num_course_mere>(.*?)</num_course_mere>");
					String distance_course = oCommonMethod.getValueByPatter(course, "<distance_course>(.*?)</distance_course>");
					String unite_dist_course = oCommonMethod.getValueByPatter(course, "<unite_dist_course>(.*?)</unite_dist_course>");
					String total_allocations = oCommonMethod.getValueByPatter(course, "<total_allocations>(.*?)</total_allocations>");
					total_allocations = total_allocations==null?null:total_allocations.replaceAll("\\.", "").replaceAll(",", "\\.");
					
					String nb_partants = oCommonMethod.getValueByPatter(course, "<nb_partants>(.*?)</nb_partants>");
					String discipline = oCommonMethod.getValueByPatter(course, "<discipline translate=\"mandatory\"\\s?>(.*?)</discipline>");
					String num_course_pmu = oCommonMethod.getValueByPatter(course, "<num_course_pmu>(.*?)</num_course_pmu>");
					String libcourt_prix_course = oCommonMethod.getValueByPatter(course, "<libcourt_prix_course>(.*?)</libcourt_prix_course>");
					String liblong_prix_course = oCommonMethod.getValueByPatter(course, "<liblong_prix_course>(.*?)</liblong_prix_course>");
					String id_nav_course = oCommonMethod.getValueByPatter(course, "<id_nav_course>(.*?)</id_nav_course>");
					String conditions_txt_course = oCommonMethod.getValueByPatter(course, "<conditions_txt_course>(.*?)</conditions_txt_course>");
					String race_cond_course = oCommonMethod.getValueByPatter(course, "<race_cond_course>(.*?)</race_cond_course>");
					String age_cond_course = oCommonMethod.getValueByPatter(course, "<age_cond_course>(.*?)</age_cond_course>");
					String sexe_cond_course = oCommonMethod.getValueByPatter(course, "<sexe_cond_course>(.*?)</sexe_cond_course>");
					String monte_cond_course = oCommonMethod.getValueByPatter(course, "<monte_cond_course>(.*?)</monte_cond_course>");
					
					Matcher partants = oCommonMethod.getMatcherStrGroup(course, "<partant>(.*?)</partant>");
					while(partants.find()){
						String partant = partants.group(1);
						String id_nav_partant = oCommonMethod.getValueByPatter(partant, "<id_nav_partant>(.*?)</id_nav_partant>");
						String num_partant = oCommonMethod.getValueByPatter(partant, "<num_partant>(.*?)</num_partant>");
						String nom_cheval = oCommonMethod.getValueByPatter(partant, "<nom_cheval>(.*?)</nom_cheval>");
						String suffixe_cheval = oCommonMethod.getValueByPatter(partant, "<suffixe_cheval>(.*?)</suffixe_cheval>");
						String sexe_partant = oCommonMethod.getValueByPatter(partant, "<sexe_partant>(.*?)</sexe_partant>");
						String joker_jockey = oCommonMethod.getValueByPatter(partant, "<joker_jockey>(.*?)</joker_jockey>");
						String age_partant = oCommonMethod.getValueByPatter(partant, "<age_partant>(.*?)</age_partant>");
						String race_partant  = oCommonMethod.getValueByPatter(partant, "<race_partant translate=\"mandatory\"\\s?>(.*?)</race_partant>");
						String robe_partant = oCommonMethod.getValueByPatter(partant, "<robe_partant translate=\"mandatory\"\\s?>(.*?)</robe_partant>");
						String date_naiss_partant = oCommonMethod.getValueByPatter(partant, "<date_naiss_partant>(.*?)</date_naiss_partant>");
						String place_corde_partant = oCommonMethod.getValueByPatter(partant, "<place_corde_partant>(.*?)</place_corde_partant>");
						String corde_demandee = oCommonMethod.getValueByPatter(partant, "<corde_demandee>(.*?)</corde_demandee>");
						String oeil_partant = oCommonMethod.getValueByPatter(partant, "<oeil_partant>(.*?)</oeil_partant>");
						String taux_reclam_partant = oCommonMethod.getValueByPatter(partant, "<taux_reclam_partant>(.*?)</taux_reclam_partant>");
						String couleur_partant = oCommonMethod.getValueByPatter(partant, "<couleur_partant translate=\"mandatory\"\\s?>(.*?)</couleur_partant >");
						String echarpe_partant = oCommonMethod.getValueByPatter(partant, "<echarpe_partant>(.*?)</echarpe_partant>");
						String ecurie_part = oCommonMethod.getValueByPatter(partant, "<ecurie_part>(.*?)</ecurie_part>");
						String dist_partant = oCommonMethod.getValueByPatter(partant, "<dist_partant>(.*?)</dist_partant>");
						String pds_calc_hand_partant = oCommonMethod.getValueByPatter(partant, "<pds_calc_hand_partant>(.*?)</pds_calc_hand_partant>");
						String pds_cond_monte_partant = oCommonMethod.getValueByPatter(partant, "<pds_cond_monte_partant>(.*?)</pds_cond_monte_partant>");
						String surcharge_partant = oCommonMethod.getValueByPatter(partant, "<surcharge_partant>(.*?)</surcharge_partant>");
						String eng_part_partant = oCommonMethod.getValueByPatter(partant, "<eng_part_partant>(.*?)</eng_part_partant>");
						String deferrer_partant = oCommonMethod.getValueByPatter(partant, "<deferrer_partant>(.*?)</deferrer_partant>");
						String ind_jum_pleine_partant = oCommonMethod.getValueByPatter(partant, "<ind_jum_pleine_partant>(.*?)</ind_jum_pleine_partant>");
						String ind_inedit_partant = oCommonMethod.getValueByPatter(partant, "<ind_inedit_partant>(.*?)</ind_inedit_partant>");
						String annee_cast_partant = oCommonMethod.getValueByPatter(partant, "<annee_cast_partant>(.*?)</annee_cast_partant>");
						String statut_part = oCommonMethod.getValueByPatter(partant, "<statut_part>(.*?)</statut_part>");
						String statut_part_pcc = oCommonMethod.getValueByPatter(partant, "<statut_part_pcc>(.*?)</statut_part_pcc>");
						String type_eng = oCommonMethod.getValueByPatter(partant, "<type_eng>(.*?)</type_eng>");
						String coul_casaque_partant = oCommonMethod.getValueByPatter(partant, "<coul_casaque_partant>(.*?)</coul_casaque_partant>");
						String disp_casaque_partant = oCommonMethod.getValueByPatter(partant, "<disp_casaque_partant>(.*?)</disp_casaque_partant>");
						String deux_coul_casaque_partant = oCommonMethod.getValueByPatter(partant, "<deux_coul_casaque_partant>(.*?)</deux_coul_casaque_partant>");
						String disp_manche_partant = oCommonMethod.getValueByPatter(partant, "<disp_manche_partant>(.*?)</disp_manche_partant>");
						String coul_manche_partant = oCommonMethod.getValueByPatter(partant, "<coul_manche_partant>(.*?)</coul_manche_partant>");
						String deux_coul_manche_partant = oCommonMethod.getValueByPatter(partant, "<deux_coul_manche_partant>(.*?)</deux_coul_manche_partant>");
						String coul_toque_partant = oCommonMethod.getValueByPatter(partant, "<coul_toque_partant>(.*?)</coul_toque_partant>");
						String disp_toque_partant = oCommonMethod.getValueByPatter(partant, "<disp_toque_partant>(.*?)</disp_toque_partant>");
						String deux_coul_toque_partant = oCommonMethod.getValueByPatter(partant, "<deux_coul_toque_partant>(.*?)</deux_coul_toque_partant>");
						String nom_pere = oCommonMethod.getValueByPatter(partant, "<nom_pere>(.*?)</nom_pere>");
						String suffixe_pere = oCommonMethod.getValueByPatter(partant, "<suffixe_pere>(.*?)</suffixe_pere>");
						String race_pere = oCommonMethod.getValueByPatter(partant, "<race_pere translate=\"mandatory\"\\s?>(.*?)</race_pere>");
						String annee_naiss_pere = oCommonMethod.getValueByPatter(partant, "<annee_naiss_pere>(.*?)</annee_naiss_pere>");
						String nom_mere = oCommonMethod.getValueByPatter(partant, "<nom_mere>(.*?)</nom_mere>");
						String suffixe_mere = oCommonMethod.getValueByPatter(partant, "<suffixe_mere>(.*?)</suffixe_mere>");
						String race_mere = oCommonMethod.getValueByPatter(partant, "<race_mere translate=\"mandatory\"\\s?>(.*?)</race_mere>");
						String annee_naiss_mere = oCommonMethod.getValueByPatter(partant, "<annee_naiss_mere>(.*?)</annee_naiss_mere>");
						String nom_pere_mere = oCommonMethod.getValueByPatter(partant, "<nom_pere_mere>(.*?)</nom_pere_mere>");
						String suffixe_pere_mere = oCommonMethod.getValueByPatter(partant, "<suffixe_pere_mere>(.*?)</suffixe_pere_mere>");
						String race_pere_mere = oCommonMethod.getValueByPatter(partant, "<race_pere_mere translate=\"mandatory\"\\s?>(.*?)</race_pere_mere>");
						String annee_naiss_pere_mere = oCommonMethod.getValueByPatter(partant, "<annee_naiss_pere_mere>(.*?)</annee_naiss_pere_mere>");
						String lieu_entrainement = oCommonMethod.getValueByPatter(partant, "<lieu_entrainement>(.*?)</lieu_entrainement>");
						String code_entraineur = oCommonMethod.getValueByPatter(partant, "<code_entraineur>(.*?)</code_entraineur>");
						String pays_entrainement = oCommonMethod.getValueByPatter(partant, "<pays_entrainement>(.*?)</pays_entrainement>");
						String departement_entrainement = oCommonMethod.getValueByPatter(partant, "<departement_entrainement>(.*?)</departement_entrainement>");
						String titre_entraineur = oCommonMethod.getValueByPatter(partant, "<titre_entraineur>(.*?)</titre_entraineur>");
						String initiales_entraineur = oCommonMethod.getValueByPatter(partant, "<initiales_entraineur>(.*?)</initiales_entraineur>");
						String prenom_entraineur = oCommonMethod.getValueByPatter(partant, "<prenom_entraineur>(.*?)</prenom_entraineur>");
						String nom_entraineur = oCommonMethod.getValueByPatter(partant, "<nom_entraineur>(.*?)</nom_entraineur>");
						String initiales_monte = oCommonMethod.getValueByPatter(partant, "<initiales_monte>(.*?)</initiales_monte>");
						String code_monte = oCommonMethod.getValueByPatter(partant, "<code_monte>(.*?)</code_monte>");
						String prenom_monte = oCommonMethod.getValueByPatter(partant, "<prenom_monte>(.*?)</prenom_monte>");
						String titre_monte = oCommonMethod.getValueByPatter(partant, "<titre_monte>(.*?)</titre_monte>");
						String nom_monte = oCommonMethod.getValueByPatter(partant, "<nom_monte>(.*?)</nom_monte>");
						String prenom_proprietaire = oCommonMethod.getValueByPatter(partant, "<prenom_proprietaire>(.*?)</prenom_proprietaire>");
						String code_proprietaire = oCommonMethod.getValueByPatter(partant, "<code_proprietaire>(.*?)</code_proprietaire>");
						String nom_proprietaire = oCommonMethod.getValueByPatter(partant, "<nom_proprietaire>(.*?)</nom_proprietaire>");
						String titre_proprietaire = oCommonMethod.getValueByPatter(partant, "<titre_proprietaire>(.*?)</titre_proprietaire>");
						String initiales_proprietaire = oCommonMethod.getValueByPatter(partant, "<initiales_proprietaire>(.*?)</initiales_proprietaire>");
						String couleurs_proprietaire = oCommonMethod.getValueByPatter(partant, "<couleurs_proprietaire translate=\"mandatory\"\\s?>(.*?)</couleurs_proprietaire>");
						String nom_eleveur = oCommonMethod.getValueByPatter(partant, "<nom_eleveur>(.*?)</nom_eleveur>");
						
						conditions_txt_course= conditions_txt_course==null?null:conditions_txt_course.replace(",", "，");
						couleur_partant= couleur_partant==null?null:couleur_partant.replace(",", "，");
						code_entraineur=code_entraineur==null?null:code_entraineur.replaceAll("[^\\d]", "");
						code_monte=code_monte==null?null:code_monte.replaceAll("[^\\d]", "");
						code_proprietaire=code_proprietaire==null?null:code_proprietaire.replaceAll("[^\\d]", "");
//						String sSql ="";
						StringBuffer sSql =new StringBuffer(); 
					     sSql=sSql.append(code_statut_infos==null?"NULL,":"N'"+code_statut_infos.replaceAll("'", "''")+"',");
					     sSql=sSql.append(libelle_statut_infos==null?"NULL,":"N'"+libelle_statut_infos.replaceAll("'", "''")+"',");
					     sSql=sSql.append(date_heure_generation==null?"NULL,":"N'"+oCommonMethod.getTime(date_heure_generation)+"',");
					     sSql=sSql.append(type_document==null?"NULL,":"N'"+type_document.replaceAll("'", "''")+"',");
					     sSql=sSql.append(date_jour_reunion==null?"NULL,":"N'"+oCommonMethod.getTime(date_jour_reunion)+"',");
					     sSql=sSql.append(libelle_hippo==null?"NULL,":"N'"+libelle_hippo.replaceAll("'", "''")+"',");
					     sSql=sSql.append(code_hippo==null?"NULL,":"N'"+code_hippo.replaceAll("'", "''")+"',");
					     sSql=sSql.append(type_reunion==null?"NULL,":"N'"+type_reunion.replaceAll("'", "''")+"',");
					     sSql=sSql.append(date_reunion==null?"NULL,":"N'"+oCommonMethod.getTime(date_reunion)+"',");
					     sSql=sSql.append(id_nav_reunion==null?"NULL,":"N'"+id_nav_reunion.replaceAll("'", "''")+"',");
					     sSql=sSql.append(num_externe_reunion==null?"NULL,":"N'"+num_externe_reunion.replaceAll("'", "''")+"',");
					     sSql=sSql.append(num_reunion==null?"NULL,":"N'"+num_reunion.replaceAll("'", "''")+"',");
					     sSql=sSql.append(num_course_mere==null?"NULL,":"N'"+num_course_mere.replaceAll("'", "''")+"',");
					     sSql=sSql.append(distance_course==null?"NULL,":"N'"+distance_course.replaceAll("'", "''")+"',");
					     sSql=sSql.append(unite_dist_course==null?"NULL,":"N'"+unite_dist_course.replaceAll("'", "''")+"',");
//					     sSql=sSql.append(total_allocations==null?"NULL,":"N'"+total_allocations.replaceAll("'", "''")+"',");
					     sSql=sSql.append( total_allocations==null?null:"N'"+total_allocations.replaceAll("'", "''")+"',");
					     sSql=sSql.append(nb_partants==null?"NULL,":"N'"+nb_partants.replaceAll("'", "''")+"',");
					     sSql=sSql.append(discipline==null?"NULL,":"N'"+discipline.replaceAll("'", "''")+"',");
					     sSql=sSql.append(num_course_pmu==null?"NULL,":"N'"+num_course_pmu.replaceAll("'", "''")+"',");
					     sSql=sSql.append(libcourt_prix_course==null?"NULL,":"N'"+libcourt_prix_course.replaceAll("'", "''")+"',");
					     sSql=sSql.append(liblong_prix_course==null?"NULL,":"N'"+liblong_prix_course.replaceAll("'", "''")+"',");
					     sSql=sSql.append(id_nav_course==null?"NULL,":"N'"+id_nav_course.replaceAll("'", "''")+"',");
					     sSql=sSql.append(conditions_txt_course==null?"NULL,":"N'"+conditions_txt_course.replaceAll("'", "''")+"',");
					     sSql=sSql.append(race_cond_course==null?"NULL,":"N'"+race_cond_course.replaceAll("'", "''")+"',");
					     sSql=sSql.append(age_cond_course==null?"NULL,":"N'"+age_cond_course.replaceAll("'", "''")+"',");
					     sSql=sSql.append(sexe_cond_course==null?"NULL,":"N'"+sexe_cond_course.replaceAll("'", "''")+"',");
					     sSql=sSql.append(monte_cond_course==null?"NULL,":"N'"+monte_cond_course.replaceAll("'", "''")+"',");
					     sSql=sSql.append(id_nav_partant==null?"NULL,":"N'"+id_nav_partant.replaceAll("'", "''")+"',");
					     sSql=sSql.append(num_partant==null?"NULL,":"N'"+num_partant.replaceAll("'", "''")+"',");
					     sSql=sSql.append(nom_cheval==null?"NULL,":"N'"+nom_cheval.replaceAll("'", "''")+"',");
					     sSql=sSql.append(suffixe_cheval==null?"NULL,":"N'"+suffixe_cheval.replaceAll("'", "''")+"',");
					     sSql=sSql.append(sexe_partant==null?"NULL,":"N'"+sexe_partant.replaceAll("'", "''")+"',");
					     sSql=sSql.append(joker_jockey==null?"NULL,":"N'"+joker_jockey.replaceAll("'", "''")+"',");
					     sSql=sSql.append(age_partant==null?"NULL,":"N'"+age_partant.replaceAll("'", "''")+"',");
					     sSql=sSql.append(race_partant==null?"NULL,":"N'"+race_partant.replaceAll("'", "''")+"',");
					     sSql=sSql.append(robe_partant==null?"NULL,":"N'"+robe_partant.replaceAll("'", "''")+"',");
					     sSql=sSql.append(date_naiss_partant==null?"NULL,":"N'"+oCommonMethod.getTime(date_naiss_partant)+"',");
					     sSql=sSql.append(place_corde_partant==null?"NULL,":"N'"+place_corde_partant.replaceAll("'", "''")+"',");
					     sSql=sSql.append(corde_demandee==null?"NULL,":"N'"+corde_demandee.replaceAll("'", "''")+"',");
					     sSql=sSql.append(oeil_partant==null?"NULL,":"N'"+oeil_partant.replaceAll("'", "''")+"',");
					     sSql=sSql.append(taux_reclam_partant==null?"NULL,":"N'"+taux_reclam_partant.replaceAll("'", "''")+"',");
					     sSql=sSql.append(couleur_partant==null?"NULL,":"N'"+couleur_partant.replaceAll("'", "''")+"',");
					     sSql=sSql.append(echarpe_partant==null?"NULL,":"N'"+echarpe_partant.replaceAll("'", "''")+"',");
					     sSql=sSql.append(ecurie_part==null?"NULL,":"N'"+ecurie_part.replaceAll("'", "''")+"',");
					     sSql=sSql.append(dist_partant==null?"NULL,":"N'"+dist_partant.replaceAll("'", "''")+"',");
					     sSql=sSql.append(pds_calc_hand_partant==null?"NULL,":"N'"+pds_calc_hand_partant.replaceAll("'", "''")+"',");
					     sSql=sSql.append(pds_cond_monte_partant==null?"NULL,":"N'"+pds_cond_monte_partant.replaceAll("'", "''")+"',");
					     sSql=sSql.append(surcharge_partant==null?"NULL,":"N'"+surcharge_partant.replaceAll("'", "''")+"',");
					     sSql=sSql.append(eng_part_partant==null?"NULL,":"N'"+eng_part_partant.replaceAll("'", "''")+"',");
					     sSql=sSql.append(deferrer_partant==null?"NULL,":"N'"+deferrer_partant.replaceAll("'", "''")+"',");
					     sSql=sSql.append(ind_jum_pleine_partant==null?"NULL,":"N'"+ind_jum_pleine_partant.replaceAll("'", "''")+"',");
					     sSql=sSql.append(ind_inedit_partant==null?"NULL,":"N'"+ind_inedit_partant.replaceAll("'", "''")+"',");
					     sSql=sSql.append(annee_cast_partant==null?"NULL,":"N'"+annee_cast_partant.replaceAll("'", "''")+"',");
					     sSql=sSql.append(statut_part==null?"NULL,":"N'"+statut_part.replaceAll("'", "''")+"',");
					     sSql=sSql.append(statut_part_pcc==null?"NULL,":"N'"+statut_part_pcc.replaceAll("'", "''")+"',");
					     sSql=sSql.append(type_eng==null?"NULL,":"N'"+type_eng.replaceAll("'", "''")+"',");
					     sSql=sSql.append(coul_casaque_partant==null?"NULL,":"N'"+coul_casaque_partant.replaceAll("'", "''")+"',");
					     sSql=sSql.append(disp_casaque_partant==null?"NULL,":"N'"+disp_casaque_partant.replaceAll("'", "''")+"',");
					     sSql=sSql.append(deux_coul_casaque_partant==null?"NULL,":"N'"+deux_coul_casaque_partant.replaceAll("'", "''")+"',");
					     sSql=sSql.append(disp_manche_partant==null?"NULL,":"N'"+disp_manche_partant.replaceAll("'", "''")+"',");
					     sSql=sSql.append(coul_manche_partant==null?"NULL,":"N'"+coul_manche_partant.replaceAll("'", "''")+"',");
					     sSql=sSql.append(deux_coul_manche_partant==null?"NULL,":"N'"+deux_coul_manche_partant.replaceAll("'", "''")+"',");
					     sSql=sSql.append(coul_toque_partant==null?"NULL,":"N'"+coul_toque_partant.replaceAll("'", "''")+"',");
					     sSql=sSql.append(disp_toque_partant==null?"NULL,":"N'"+disp_toque_partant.replaceAll("'", "''")+"',");
					     sSql=sSql.append(deux_coul_toque_partant==null?"NULL,":"N'"+deux_coul_toque_partant.replaceAll("'", "''")+"',");
					     sSql=sSql.append(nom_pere==null?"NULL,":"N'"+nom_pere.replaceAll("'", "''")+"',");
					     sSql=sSql.append(suffixe_pere==null?"NULL,":"N'"+suffixe_pere.replaceAll("'", "''")+"',");
					     sSql=sSql.append(race_pere==null?"NULL,":"N'"+race_pere.replaceAll("'", "''")+"',");
					     sSql=sSql.append(annee_naiss_pere==null?"NULL,":"N'"+annee_naiss_pere.replaceAll("'", "''")+"',");
					     sSql=sSql.append(nom_mere==null?"NULL,":"N'"+nom_mere.replaceAll("'", "''")+"',");
					     sSql=sSql.append(suffixe_mere==null?"NULL,":"N'"+suffixe_mere.replaceAll("'", "''")+"',");
					     sSql=sSql.append(race_mere==null?"NULL,":"N'"+race_mere.replaceAll("'", "''")+"',");
					     sSql=sSql.append(annee_naiss_mere==null?"NULL,":"N'"+annee_naiss_mere.replaceAll("'", "''")+"',");
					     sSql=sSql.append(nom_pere_mere==null?"NULL,":"N'"+nom_pere_mere.replaceAll("'", "''")+"',");
					     sSql=sSql.append(suffixe_pere_mere==null?"NULL,":"N'"+suffixe_pere_mere.replaceAll("'", "''")+"',");
					     sSql=sSql.append(race_pere_mere==null?"NULL,":"N'"+race_pere_mere.replaceAll("'", "''")+"',");
					     sSql=sSql.append(annee_naiss_pere_mere==null?"NULL,":"N'"+annee_naiss_pere_mere.replaceAll("'", "''")+"',");
					     sSql=sSql.append(lieu_entrainement==null?"NULL,":"N'"+lieu_entrainement.replaceAll("'", "''")+"',");
					     sSql=sSql.append(code_entraineur==null?"NULL,":"N'"+code_entraineur.replaceAll("'", "''")+"',");
					     sSql=sSql.append(pays_entrainement==null?"NULL,":"N'"+pays_entrainement.replaceAll("'", "''")+"',");
					     sSql=sSql.append(departement_entrainement==null?"NULL,":"N'"+departement_entrainement.replaceAll("'", "''")+"',");
					     sSql=sSql.append(titre_entraineur==null?"NULL,":"N'"+titre_entraineur.replaceAll("'", "''")+"',");
					     sSql=sSql.append(initiales_entraineur==null?"NULL,":"N'"+initiales_entraineur.replaceAll("'", "''")+"',");
					     sSql=sSql.append(prenom_entraineur==null?"NULL,":"N'"+prenom_entraineur.replaceAll("'", "''")+"',");
					     sSql=sSql.append(nom_entraineur==null?"NULL,":"N'"+nom_entraineur.replaceAll("'", "''")+"',");
					     sSql=sSql.append(initiales_monte==null?"NULL,":"N'"+initiales_monte.replaceAll("'", "''")+"',");
					     sSql=sSql.append(code_monte==null?"NULL,":"N'"+code_monte.replaceAll("'", "''")+"',");
					     sSql=sSql.append(prenom_monte==null?"NULL,":"N'"+prenom_monte.replaceAll("'", "''")+"',");
					     sSql=sSql.append(titre_monte==null?"NULL,":"N'"+titre_monte.replaceAll("'", "''")+"',");
					     sSql=sSql.append(nom_monte==null?"NULL,":"N'"+nom_monte.replaceAll("'", "''")+"',");
					     sSql=sSql.append(prenom_proprietaire==null?"NULL,":"N'"+prenom_proprietaire.replaceAll("'", "''")+"',");
					     sSql=sSql.append(code_proprietaire==null?"NULL,":"N'"+code_proprietaire.replaceAll("'", "''")+"',");
					     sSql=sSql.append(nom_proprietaire==null?"NULL,":"N'"+nom_proprietaire.replaceAll("'", "''")+"',");
					     sSql=sSql.append(titre_proprietaire==null?"NULL,":"N'"+titre_proprietaire.replaceAll("'", "''")+"',");
					     sSql=sSql.append(initiales_proprietaire==null?"NULL,":"N'"+initiales_proprietaire.replaceAll("'", "''")+"',");
					     sSql=sSql.append(couleurs_proprietaire==null?"NULL,":"N'"+couleurs_proprietaire.replaceAll("'", "''")+"',");
					     sSql=sSql.append(nom_eleveur==null?"NULL,":"N'"+nom_eleveur.replaceAll("'", "''")+"',");
							
						sSql=sSql.append("'"+oCommonMethod.getCurrentTime()+"',");
						sSql=sSql.append("N'"+fileName.replace(Controller.sSaveFilePath,"")+"'");
					     logger.info("pr_PmuInfoCentreKafka_XML_Runners_InsertData  " + sSql);
					     oCommonDB.execStoredProcedures("pr_PmuInfoCentreKafka_XML_Runners_InsertData", sSql.toString());
					     sSql=null;
					}
				}
			}
		}
	}
		catch (Exception e) 
		{
			logger.error("error fileName:"+fileName+"\n\n",e);
		}
	}
	
	public void run(String pathName)
	{
			try {
				List<String> fileList = FileDispose.readLocalFileDir(pathName);
				String bak_path = pathName+File.separator+"ExtractedFiles"+File.separator;
				 File dir = new File(bak_path);  
				 if(!dir.exists())
					 CommonMethod.createDir(bak_path);
				for(int i=0;i<fileList.size();i++)
				{
					 String fileName= fileList.get(i);
					 parseFile(fileName);
				}
				
			} catch (Exception e) {
				logger.error("",e);
		}
	}
	public static void main(String[] args) {
		PropertyConfigurator.configure("log4j.properties");
		String filePath ="D:\\Denis\\PMU XPRO APP\\XML\\06_PARTANTS";
		new ParseRunners06Bll().run(filePath);
	}
}
