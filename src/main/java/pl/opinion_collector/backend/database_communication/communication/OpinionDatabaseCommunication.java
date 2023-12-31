package pl.opinion_collector.backend.database_communication.communication;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import pl.opinion_collector.backend.database_communication.listener.ProductListener;
import pl.opinion_collector.backend.database_communication.model.Opinion;
import pl.opinion_collector.backend.database_communication.repository.OpinionRepository;

import java.util.List;

@Component
@Transactional
public class OpinionDatabaseCommunication {

    @Autowired
    private OpinionRepository opinionRepository;

    @Autowired
    private ProductDatabaseCommunication productDatabaseCommunication;

    @Autowired
    private UserDatabaseCommunication userDatabaseCommunication;

    public List<Opinion> getProductOpinions(String sku) {
        return opinionRepository.findAllByProductId(productDatabaseCommunication.getProductBySku(sku));
    }

    public Opinion addProductOpinion(Integer opinionValue, String opinionDescription, String opinionPicture, List<String> advantages, List<String> disadvantages, String sku, Long userId) {
        ProductListener productListener = new ProductListener();
        Opinion opinion = new Opinion(opinionValue, opinionDescription, opinionPicture, advantages, disadvantages, userDatabaseCommunication.getUserById(userId), productDatabaseCommunication.getProductBySku(sku));
        Opinion added = opinionRepository.save(opinion);
        productListener.calculateAvgOpinion(productDatabaseCommunication.getProductBySku(sku));
        return added;
    }

    public List<Opinion> getUserOpinions(Long userId) {
        return opinionRepository.findAllByUserId(userDatabaseCommunication.getUserById(userId));
    }


}
