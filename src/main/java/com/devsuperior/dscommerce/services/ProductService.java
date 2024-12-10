package com.devsuperior.dscommerce.services;


import com.devsuperior.dscommerce.dto.ProductDto;
import com.devsuperior.dscommerce.entities.Product;
import com.devsuperior.dscommerce.repositories.ProductRepository;
import com.devsuperior.dscommerce.services.exceptions.DatabaseException;
import com.devsuperior.dscommerce.services.exceptions.ResourceNotFoundException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProductService {

    @Autowired
    private ProductRepository repository;

    @Transactional(readOnly = true)
    public ProductDto findById(Long id){
        Product product = repository.findById(id).get();
        return new ProductDto(product);
    }

    @Transactional(readOnly = true)
        public Page<ProductDto> findAll(Pageable pageable){
           Page<Product> result = repository.findAll(pageable);
           return result.map(ProductDto::new);
        }

        @Transactional
    public ProductDto insert(ProductDto dto){
        Product product = new Product();
        copyDtoToEntity(dto, product);
        product = repository.save(product);
        return new ProductDto(product);
        }

    @Transactional
    public ProductDto update(Long id, ProductDto dto){
        try{
            Product product = repository.getReferenceById(id);
            copyDtoToEntity(dto, product);
            product = repository.save(product);
            return new ProductDto(product);
        } catch (EntityNotFoundException e){
            throw new ResourceNotFoundException("recurso não encontrado");
        }

    }

    @Transactional(propagation = Propagation.SUPPORTS)
    public void delete(Long id){
        if (!repository.existsById(id)){
            throw new ResourceNotFoundException("Recurso não encontrado");
        }
        try{
            repository.deleteById(id);
        } catch (DataIntegrityViolationException e){
            throw new DatabaseException("Falha de integridade referencial");
        }


    }

    private void copyDtoToEntity(ProductDto dto, Product product){
        product.setName(dto.getName());
        product.setDescription(dto.getDescription());
        product.setPrice(dto.getPrice());
        product.setImgUrl(dto.getImgUrl());
    }

}
