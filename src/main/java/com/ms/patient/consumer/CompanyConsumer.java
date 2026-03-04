package com.ms.patient.consumer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.ms.patient.events.CompanyCreatedEvent;
import com.ms.patient.models.CompanyProfile;
import com.ms.patient.repositories.CompanyProfileRepository;

import io.awspring.cloud.sqs.annotation.SqsListener;

@Component
public class CompanyConsumer {

    @Autowired
    private CompanyProfileRepository repository;

    @SqsListener(value = "${medcare.aws.sqs.queue.companty.register}")
    public void receiveCompanyInfo(CompanyCreatedEvent event){
        
        CompanyProfile profile = new CompanyProfile();
        profile.setId(event.id());
        profile.setName(event.name());
        profile.setRegistered(false);

        repository.save(profile);
        System.out.println("Perfil inicial da empresa criado");
    }
    
}
