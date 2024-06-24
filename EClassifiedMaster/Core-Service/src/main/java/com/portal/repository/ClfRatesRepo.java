package com.portal.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.portal.clf.entities.ClfRates;

public interface ClfRatesRepo extends CrudRepository<ClfRates, String>{

//	@Query("from ClfRates gda where classifiedAdsType = ?1 and editionId in(?2) and gda.markAsDelete = false")      
//	List<ClfRates> getRates(Integer adsType, List<Integer> editions);
	
	@Query("from ClfRates gda where classifiedAdsType = ?1 and editionId in(?2) and classifiedAdsSubtype = ?3 and gda.markAsDelete = false")      
	List<ClfRates> getRates(Integer adsType, List<Integer> editions, Integer adsSubtype);
	
	@Query("from ClfRates cr where cr.classifiedAdsType = ?1 and cr.classifiedAdsSubtype = ?2 and cr.editionId = ?3 and cr.markAsDelete = false")
	List<ClfRates> getClfRates(Integer adsType,Integer adsSubtype,Integer editions);
	
}
