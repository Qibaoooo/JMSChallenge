package nus.iss.team11.controller.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import nus.iss.team11.model.AzureImage;
import nus.iss.team11.model.OwnerVerification;
import nus.iss.team11.repository.OwnerVerificationRepository;

@Service
public class OwnerVerificationServiceImpl implements OwnerVerificationService {

	@Autowired
	OwnerVerificationRepository ownerVerificationRepository;
	@Override
	public List<OwnerVerification> findAllOVs() {
		return ownerVerificationRepository.findAll();
	}
	
	@Override
	public OwnerVerification saveOwnerVerification (OwnerVerification ownerVerification) {
		return ownerVerificationRepository.saveAndFlush(ownerVerification);
	}
	
	@Override
	public OwnerVerification getOwnerVerificationById (int id) {
		return ownerVerificationRepository.getReferenceById(id);
	}
	
	@Override
	public void deleteVerification(int id) {
		ownerVerificationRepository.deleteById(id);
	}

	
}
