package nus.iss.team11.controller.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import nus.iss.team11.model.AzureImage;
import nus.iss.team11.repository.AzureImageRepository;

@Service
public class AzureImageServiceImpl implements AzureImageService{
	@Autowired
	AzureImageRepository azureImageRepository;
	
	@Override
	public AzureImage findImageByFileName(String fileName) {
		return azureImageRepository.findByFileName(fileName);
	}
	
	@Override
	public AzureImage saveImage(AzureImage azureImage) {
		return azureImageRepository.save(azureImage);
	}
	
	@Override
	public List<AzureImage> findAll(){
		return azureImageRepository.findAll();
	}

}
