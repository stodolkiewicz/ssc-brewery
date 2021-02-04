package guru.sfg.brewery.web.controllers;

import guru.sfg.brewery.domain.Beer;
import guru.sfg.brewery.repositories.BeerRepository;
import guru.sfg.brewery.web.model.BeerStyleEnum;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
public class BeerRestControllerIT extends BaseIT {

    @Autowired
    BeerRepository beerRepository;

    public Beer beerToDelete() {
        return beerRepository.saveAndFlush(Beer.builder()
            .beerName("Delete beer")
            .beerStyle(BeerStyleEnum.IPA)
            .minOnHand(12)
            .quantityToBrew(200)
            .build()
        );
    }

    @Test
    void listBreweriesCustomer() throws Exception {
        mockMvc.perform(
                get("/brewery/breweries")
                        .with(httpBasic("scott", "tiger")))
                .andExpect(status().is2xxSuccessful());
    }

    @Test
    void listBreweriesAdmin() throws Exception {
        mockMvc.perform(
                get("/brewery/breweries")
                        .with(httpBasic("spring", "guru")))
                .andExpect(status().is2xxSuccessful());
    }

    @Test
    void deleteBeerBadCreds() throws Exception {
        mockMvc.perform(
                delete("/api/v1/beer/" + beerToDelete().getId())
                        .header("Api-Key", "spring")
                        .header("Api-Secret", "guruXXX")
        )
                .andExpect(status().isUnauthorized());
    }

    @Test
    void deleteBeerHttpBasicUserRole() throws Exception {
        mockMvc.perform(
                delete("/api/v1/beer/cde503c2-4c95-47eb-a07b-5751bb04f779")
                .with(httpBasic("user", "password")))
                .andExpect(status().isForbidden());
    }

    @Test
    void deleteBeerHttpBasicCustomerRole() throws Exception {
        mockMvc.perform(
                delete("/api/v1/beer/cde503c2-4c95-47eb-a07b-5751bb04f779")
                        .with(httpBasic("scott", "tiger")))
                .andExpect(status().isForbidden());
    }

    @Test
    void deleteBeer() throws Exception {
        mockMvc.perform(
                    delete("/api/v1/beer/cde503c2-4c95-47eb-a07b-5751bb04f779")
                        .header("Api-Key", "spring")
                        .header("Api-Secret", "guru")
                )
                .andExpect(status().isOk());
    }

    @Test
    void findBeers() throws Exception {
        mockMvc.perform(get("/api/v1/beer"))
                .andExpect(status().isOk());
    }

    @Test
    void findBeersById() throws Exception {
        mockMvc.perform(get("/api/v1/beer/f4a3973d-3e11-4fbd-8446-28cb94a190dd"))
                .andExpect(status().isOk());
    }

    @Test
    void findBeersByUpc() throws Exception {
        mockMvc.perform(get("/api/v1/beerUpc/0631234200036"))
                .andExpect(status().isOk());
    }

}
