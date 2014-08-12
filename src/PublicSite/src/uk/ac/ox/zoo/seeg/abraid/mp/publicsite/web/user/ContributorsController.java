package uk.ac.ox.zoo.seeg.abraid.mp.publicsite.web.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import uk.ac.ox.zoo.seeg.abraid.mp.common.service.core.ExpertService;

/**
 * foo
 * Copyright (c) 2014 University of Oxford
 */
@Controller
public class ContributorsController {
    public static final int PAGE_SIZE = 20;
    private ExpertService expertService;

    @Autowired
    public ContributorsController(ExpertService expertService) {
        this.expertService = expertService;
    }

    /**
     * Shows the experts page.
     * @return The ftl page name.
     */
    @RequestMapping(value = "/experts", method = RequestMethod.GET)
    public String showExperts(ModelMap model, Integer page) {
        long pageCount = ((expertService.getCountOfPubliclyVisibleExperts() + PAGE_SIZE - 1) / PAGE_SIZE);
        pageCount = (pageCount == 0) ? 1 : pageCount;
        int pageNumber = validatePageNumber(page, pageCount) ? page : 1;


        model.addAttribute("page", expertService.getPageOfPubliclyVisibleExperts(pageNumber, PAGE_SIZE));
        model.addAttribute("pageCount", pageCount);
        model.addAttribute("pageNumber", pageNumber);

        return "experts";
    }

    private boolean validatePageNumber(Integer page, long pageCount) {
        return page != null && page >= 1 && page <= pageCount;
    }
}
