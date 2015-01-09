package org.freeshr.journal.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class PatientJournalController {
    @RequestMapping(value="/journal/{healthId}", method = RequestMethod.GET)
    public @ResponseBody
    String journal(@PathVariable("healthId")
                   String healthId) {
        return "hello world " + healthId;
    }
}
