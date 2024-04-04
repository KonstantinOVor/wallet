package com.example.service.wallet.controllers;

import com.example.service.wallet.dto.WalletDTO;
import com.example.service.wallet.exception.WalletNotFoundException;
import com.example.service.wallet.mapper.WalletMapper;
import com.example.service.wallet.model.OperationType;
import com.example.service.wallet.model.Wallet;
import com.example.service.wallet.service.WalletService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Slf4j
public class WalletControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private WalletService walletService;
    @MockBean
    private WalletMapper walletMapper;
    @Autowired
    protected ObjectMapper objectMapper;

    @Test
    public void testCreateWallet_validInput_returnsCreated() throws Exception {
        WalletDTO walletDto = new WalletDTO(1L, "DEPOSIT", 100L);

        String value = "1";
        byte[] bytes = value.getBytes();
        UUID uuid = UUID.nameUUIDFromBytes(bytes);
        Wallet expectedWallet = Wallet.builder()
                .id(uuid)
                .type(OperationType.DEPOSIT)
                .amount(BigDecimal.valueOf(100L))
                .build();

        Mockito.when(walletMapper.mapWalletDTOToWallet(walletDto)).thenReturn(expectedWallet);

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/wallet")
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                        .content(asJsonString(walletDto)))
                        .andExpect(status().isCreated())
                        .andReturn();

        String response = result.getResponse().getContentAsString();

        assertEquals(asJsonString(expectedWallet), response);

        Mockito.verify(walletService, times(1)).processWalletOperation(walletDto);
    }

    @Test
    public void testCreateWallet_invalidInput_returnsBadRequest() throws Exception {
        WalletDTO invalidWalletDto = new WalletDTO(1L,null, 100L);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/wallet")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(invalidWalletDto)))
                        .andExpect(status().isBadRequest());

        Mockito.verify(walletService, times(0)).processWalletOperation(invalidWalletDto);
    }

    @Test
    public void testGetWallets_returnsOk() throws Exception {
        List<Wallet> wallets = Arrays.asList(new Wallet(UUID.randomUUID(), OperationType.DEPOSIT, BigDecimal.valueOf(100)),
                new Wallet(UUID.randomUUID(), OperationType.WITHDRAW, BigDecimal.valueOf(50)));

        Mockito.when(walletService.getWallets()).thenReturn(wallets);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/wallet"))
                .andExpect(status().isOk())
                .andExpect(content().string(asJsonString(wallets)));
    }

    @Test
    public void testGetBalanceOfWallet_validId_returnsOk() throws Exception {
        Long walletId = 1L;
        BigDecimal balance = BigDecimal.valueOf(100);
        Mockito.when(walletService.getWalletBalance(walletId)).thenReturn(balance);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/wallet/{walletId}", walletId))
                .andExpect(status().isOk())
                .andExpect(content().string(balance.toString()));
    }

    @Test
    public void testGetBalanceOfWallet_notFound_returnsNotFound() throws Exception {
        Long walletId = null;

        Mockito.when(walletService.getWalletBalance(walletId)).thenThrow(new WalletNotFoundException("Wallet not found"));

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/wallet/{walletId}", walletId))
                .andExpect(status().isNotFound());
    }


    @Test
    public void testDeleteWallet_existingWallet_returnsNoContent() throws Exception {
        Long walletId = 1L;

        Mockito.doNothing().when(walletService).deleteWallet(walletId);

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/wallet/{walletId}", walletId))
                .andExpect(status().isNoContent());
    }

    String asJsonString(Object obj) throws JsonProcessingException {
        return objectMapper.writeValueAsString(obj);
    }
}

