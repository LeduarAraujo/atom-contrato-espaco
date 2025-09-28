package com.atom.contratoespaco.controller;

import com.atom.contratoespaco.dto.EspacoDTO;
import com.atom.contratoespaco.service.EspacoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Base64;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/espacos")
@RequiredArgsConstructor
public class EspacoController {

    private final EspacoService espacoService;
    private static final String UPLOAD_DIR = "uploads/";

    @GetMapping("/test")
    public ResponseEntity<String> test() {
        return ResponseEntity.ok("API funcionando!");
    }

    @GetMapping
    public ResponseEntity<List<EspacoDTO>> listarEspacos() {
        try {
            List<EspacoDTO> espacos = espacoService.listarEspacos();
            return ResponseEntity.ok(espacos);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<EspacoDTO> buscarEspacoPorId(@PathVariable Long id) {
        return espacoService.buscarEspacoPorId(id)
                .map(espaco -> ResponseEntity.ok(espaco))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/buscar")
    public ResponseEntity<List<EspacoDTO>> buscarEspacosPorNome(@RequestParam String nome) {
        List<EspacoDTO> espacos = espacoService.buscarEspacosPorNome(nome);
        return ResponseEntity.ok(espacos);
    }

    @PostMapping
    public ResponseEntity<EspacoDTO> criarEspaco(@RequestBody EspacoDTO espacoDTO) {
        try {
            EspacoDTO espacoCriado = espacoService.criarEspaco(espacoDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(espacoCriado);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping(value = "/com-imagem", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<EspacoDTO> criarEspacoComImagem(
            @RequestParam("nome") String nome,
            @RequestParam(value = "logo", required = false) MultipartFile logo,
            @RequestParam(value = "nomeProprietario", required = false) String nomeProprietario,
            @RequestParam(value = "cnpjProprietario", required = false) String cnpjProprietario) {
        try {
            System.out.println("Criando espaço com nome: " + nome);
            System.out.println("Nome do proprietário: " + nomeProprietario);
            System.out.println("CNPJ do proprietário: " + cnpjProprietario);
            System.out.println("Arquivo recebido: " + (logo != null ? logo.getOriginalFilename() : "nenhum"));

            EspacoDTO espacoDTO = new EspacoDTO();
            espacoDTO.setNome(nome);
            espacoDTO.setNomeProprietario(nomeProprietario);
            espacoDTO.setCnpjProprietario(cnpjProprietario);

            // Se há um arquivo, salvar como BLOB no banco
            if (logo != null && !logo.isEmpty()) {
                System.out.println("Salvando arquivo como BLOB: " + logo.getOriginalFilename());
                byte[] logoData = logo.getBytes();
                String mimeType = logo.getContentType();
                
                // Salvar como BLOB no banco
                espacoDTO.setLogoData(logoData);
                espacoDTO.setLogoMimeType(mimeType);
                espacoDTO.setLogoUrl(null); // Não usar mais logoUrl
                
                System.out.println("Logo salvo como BLOB, tamanho: " + logoData.length + " bytes, MIME: " + mimeType);
            }

            EspacoDTO espacoCriado = espacoService.criarEspaco(espacoDTO);
            System.out.println("Espaço criado com sucesso: " + espacoCriado.getId());
            return ResponseEntity.status(HttpStatus.CREATED).body(espacoCriado);

        } catch (Exception e) {
            System.err.println("Erro ao criar espaço: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    private String salvarArquivo(MultipartFile file) throws IOException {
        // Criar diretório se não existir
        Path uploadPath = Paths.get(UPLOAD_DIR);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        // Gerar nome único para o arquivo
        String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
        Path filePath = uploadPath.resolve(fileName);

        // Salvar arquivo
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        // Retornar URL do arquivo para compatibilidade com Angular
        return "/uploads/" + fileName;
    }

    private String salvarImagemTemporaria(MultipartFile file) throws IOException {
        // Caminho para a pasta assets/temp do Angular
        String tempDir = "../ang-contrato-espaco/src/assets/temp";
        Path tempPath = Paths.get(tempDir);
        
        // Criar diretório se não existir
        if (!Files.exists(tempPath)) {
            Files.createDirectories(tempPath);
        }

        // Gerar nome único para o arquivo
        String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
        Path filePath = tempPath.resolve(fileName);

        // Salvar arquivo
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        // Retornar URL relativa para o Angular
        return "assets/temp/" + fileName;
    }

    @PutMapping("/{id}")
    public ResponseEntity<EspacoDTO> atualizarEspaco(@PathVariable Long id, @RequestBody EspacoDTO espacoDTO) {
        try {
            EspacoDTO espacoAtualizado = espacoService.atualizarEspaco(id, espacoDTO);
            return ResponseEntity.ok(espacoAtualizado);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluirEspaco(@PathVariable Long id) {
        try {
            espacoService.excluirEspaco(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/{id}/logo")
    public ResponseEntity<String> getLogoBase64(@PathVariable Long id) {
        try {
            Optional<EspacoDTO> espacoOpt = espacoService.buscarEspacoPorId(id);
            if (espacoOpt.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            EspacoDTO espaco = espacoOpt.get();
            if (espaco.getLogoData() == null) {
                return ResponseEntity.notFound().build();
            }

            String base64Image = Base64.getEncoder().encodeToString(espaco.getLogoData());
            String mimeType = espaco.getLogoMimeType() != null ? espaco.getLogoMimeType() : "image/png";
            String dataUrl = "data:" + mimeType + ";base64," + base64Image;
            
            return ResponseEntity.ok(dataUrl);
        } catch (Exception e) {
            System.err.println("Erro ao obter logo: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

}