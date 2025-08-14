package com.example.bilisimgarajitask.brand;

import com.example.bilisimgarajitask.common.NotFoundException;
import com.example.bilisimgarajitask.organization.OrganizationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

@Service @RequiredArgsConstructor @Transactional
public class BrandService {
    private final BrandRepository repoBrand;

    private final OrganizationRepository repoOrg;
    private static final String PREFIX="BR-";
    private static final String ALPHANUM="ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private final Random rnd=new Random();

    public BrandResponse create(BrandCreateRequest r){
        Brand b=new Brand();
        b.setName(r.name());
        b.setDescription(r.description());
        b.setActive(r.active()==null || r.active());
        b.setCode(generateUniqueCode());
        return BrandMapper.toResponse(repoBrand.save(b));
    }
    public BrandResponse get(UUID id){
        Brand b= repoBrand.findById(id).orElseThrow(()->new NotFoundException("Brand not found: "+id));
        return BrandMapper.toResponse(b);
    }
    public List<BrandResponse> list(){
        return repoBrand.findAll(Sort.by(Sort.Direction.DESC,"createdAt"))
                .stream().map(BrandMapper::toResponse).toList();
    }
    public BrandResponse update(UUID id, BrandUpdateRequest r){
        Brand b= repoBrand.findById(id).orElseThrow(()->new NotFoundException("Brand not found: "+id));
        BrandMapper.applyUpdate(b,r);
        return BrandMapper.toResponse(b);
    }
    public void delete(UUID id) {
        Brand b = repoBrand.findById(id)
                .orElseThrow(() -> new NotFoundException("Brand not found: " + id));

        long orgCount = repoOrg.countByBrandId(b.getId());
        if (orgCount > 0) {
            throw new ResponseStatusException(BAD_REQUEST,"Brand has organizations; delete them first");
        }
        repoBrand.delete(b);
    }

    private String generateUniqueCode(){
        String code; int tries=0;
        do{
            code=PREFIX+randomToken(8); tries++;
            if(tries>10) code=PREFIX+UUID.randomUUID().toString().substring(0,8).toUpperCase();
        }while(repoBrand.existsByCode(code));
        return code;
    }
    private String randomToken(int len){
        StringBuilder sb=new StringBuilder(len);
        for(int i=0;i<len;i++) sb.append(ALPHANUM.charAt(rnd.nextInt(ALPHANUM.length())));
        return sb.toString();
    }


}