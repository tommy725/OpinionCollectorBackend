package pl.opinion_collector.backend.database_communication.model;

import lombok.*;
import pl.opinion_collector.backend.database_communication.listener.ProductListener;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@EntityListeners(ProductListener.class)
@Setter
@Getter
@NoArgsConstructor
@RequiredArgsConstructor
@Table(name = "product")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_id")
    private Long productId;

    @Column(unique = true)
    @NonNull
    private String sku;

    @NonNull
    private String name;

    @NonNull
    private String pictureUrl;

    @NonNull
    private String description;

    @NonNull
    private Boolean visible;

    @Column(name = "opinion_avg")
    private Double opinionAvg;

    @NonNull
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User authorId;

    @OneToMany(mappedBy = "productId", targetEntity = Opinion.class, cascade = CascadeType.ALL)
    private List<Opinion> opinions = new ArrayList<>();

    @OneToMany(mappedBy = "productId", targetEntity = Suggestion.class, cascade = CascadeType.ALL)
    private List<Suggestion> suggestions = new ArrayList<>();

    @NonNull
    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.DETACH)
    @JoinTable(
            name = "category_products",
            joinColumns = @JoinColumn(name = "product_id"),
            inverseJoinColumns = @JoinColumn(name = "category_id"))
    private List<Category> categories;
}
