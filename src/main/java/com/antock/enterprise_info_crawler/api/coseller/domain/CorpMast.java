package com.antock.enterprise_info_crawler.api.coseller.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name= "corp_mast" , uniqueConstraints = {
        @UniqueConstraint(
                name="CORP_MAST_UNIQUE",
                columnNames = {"biz_no"}
        )
})
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class CorpMast {

    @Id
    @GeneratedValue( strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable=false)
    private String sellerId;

    @Column(nullable=false)
    private String bizNm;

    @Column(nullable=false, unique = true)
    private String bizNo;

    private String corpRegNo;

    private String regionCd;
}
