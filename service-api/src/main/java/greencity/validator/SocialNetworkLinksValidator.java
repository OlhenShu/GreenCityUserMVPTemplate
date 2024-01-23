package greencity.validator;

import greencity.annotations.ValidSocialNetworkLink;
import greencity.constant.ErrorMessage;
import greencity.constant.ValidationConstants;
import greencity.exception.exceptions.BadSocialNetworkLinksException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class SocialNetworkLinksValidator implements ConstraintValidator<ValidSocialNetworkLink, List<String>> {
    @Override
    public void initialize(ValidSocialNetworkLink constraint) {
        // Initializes the validator in preparation for #isValid calls
    }

    @Override
    public boolean isValid(List<String> links, ConstraintValidatorContext context) {
        if (links == null) {
            return true;
        }
        if (links.size() > ValidationConstants.MAX_AMOUNT_OF_SOCIAL_NETWORK_LINKS) {
            throw new BadSocialNetworkLinksException(ErrorMessage.USER_CANNOT_ADD_MORE_THAN_5_SOCIAL_NETWORK_LINKS);
        }
        if (!onlyUniqueLinks(links)) {
            throw new BadSocialNetworkLinksException(ErrorMessage.USER_CANNOT_ADD_SAME_SOCIAL_NETWORK_LINKS);
        }
        return links.stream().allMatch(UrlValidator::isUrlValid);
    }

    private boolean onlyUniqueLinks(List<String> list) {
        Set<String> hashSet = new HashSet<>(list);
        return (hashSet.size() == list.size());
    }
}