package nus.iss.team11.controller.service;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import nus.iss.team11.model.CatSighting;
import nus.iss.team11.repository.AzureImageRepository;
import nus.iss.team11.repository.CatSightingRepository;

@Service
public class CatSightingServiceImpl implements CatSightingService {

	@Autowired
	CatSightingRepository catSightingRepository;
	
	@Autowired
	AzureImageRepository azureImageRepository;

	@Override
	public List<CatSighting> getAllCatSightings() {
		return catSightingRepository.findAll();
	}
	
	@Override
	public CatSighting getCatSightingById(int id) {
		return catSightingRepository.getReferenceById(id);
	}
	
	@Override
	public CatSighting saveSighting(CatSighting Sighting){
		return catSightingRepository.saveAndFlush(Sighting);
	}

	@Override
	public void deleteSighting(int id) {
		azureImageRepository.deleteAllByCatSightingId(id);
		catSightingRepository.deleteById(id);
	}

}
