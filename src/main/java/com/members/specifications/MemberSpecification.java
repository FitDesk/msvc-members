package com.members.specifications;

import com.members.entity.MemberEntity;
import org.springframework.data.jpa.domain.Specification;

public class MemberSpecification {

    public static Specification<MemberEntity> hasDni(String dni) {
        return ((root, query, criteriaBuilder) ->
                dni == null
                        ? null
                        : criteriaBuilder.equal(root.get("dni"), dni)
        );
    }

}
