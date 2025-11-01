package com.members.specifications;

import com.members.entity.MemberEntity;
import com.members.entity.MembershipEntity;
import com.members.enums.MembershipStatus;
import jakarta.persistence.criteria.*;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;

public class MemberSpecification {

    public static Specification<MemberEntity> hasDni(String dni) {
        return (root, query, cb) -> dni == null || dni.isBlank()
                ? null
                : cb.equal(root.get("dni"), dni);
    }

    public static Specification<MemberEntity> hasEmail(String email) {
        return (root, query, cb) -> email == null || email.isBlank()
                ? null
                : cb.equal(cb.lower(root.get("email")), email.toLowerCase());
    }

    public static Specification<MemberEntity> hasFirstName(String firstName) {
        return (root, query, cb) -> firstName == null || firstName.isBlank()
                ? null
                : cb.like(cb.lower(root.get("firstName")), "%" + firstName.toLowerCase() + "%");
    }

    public static Specification<MemberEntity> hasLastName(String lastName) {
        return (root, query, cb) -> lastName == null || lastName.isBlank()
                ? null
                : cb.like(cb.lower(root.get("lastName")), "%" + lastName.toLowerCase() + "%");
    }

    public static Specification<MemberEntity> globalSearch(String search) {
        return (root, query, cb) -> {
            if (search == null || search.isBlank()) {
                return null;
            }

            String searchPattern = "%" + search.toLowerCase() + "%";

            return cb.or(
                    cb.like(cb.lower(root.get("dni")), searchPattern),
                    cb.like(cb.lower(root.get("email")), searchPattern),
                    cb.like(cb.lower(root.get("firstName")), searchPattern),
                    cb.like(cb.lower(root.get("lastName")), searchPattern));
        };
    }

    public static Specification<MemberEntity> hasMembershipStatus(MembershipStatus status) {
        return (root, query, cb) -> {
            if (status == null) {
                return null;
            }

            Join<MemberEntity, MembershipEntity> membershipJoin = root.join("memberships", JoinType.LEFT);

            Predicate statusPredicate = cb.equal(membershipJoin.get("status"), status);

            if (status == MembershipStatus.ACTIVE) {
                Predicate notExpired = cb.greaterThan(
                        membershipJoin.get("endDate"),
                        LocalDateTime.now());
                return cb.and(statusPredicate, notExpired);
            }

            return statusPredicate;
        };
    }

    public static Specification<MemberEntity> hasActiveMembership() {
        return (root, query, cb) -> {
            Join<MemberEntity, MembershipEntity> membershipJoin = root.join("memberships", JoinType.INNER);

            return cb.and(
                    cb.equal(membershipJoin.get("status"), MembershipStatus.ACTIVE),
                    cb.greaterThan(membershipJoin.get("endDate"), LocalDateTime.now()));
        };
    }

    public static Specification<MemberEntity> membershipExpiringInDays(int days) {
        return (root, query, cb) -> {
            Join<MemberEntity, MembershipEntity> membershipJoin = root.join("memberships", JoinType.INNER);
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime futureDate = now.plusDays(days);

            return cb.and(
                    cb.equal(membershipJoin.get("status"), MembershipStatus.ACTIVE),
                    cb.between(membershipJoin.get("endDate"), now, futureDate));
        };
    }
}