package cn.ljcljc.document.controller;

import cn.ljcljc.common.domain.Result;
import cn.ljcljc.document.domain.dto.DocDTO;
import cn.ljcljc.document.service.DocService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Tag(name = "个人履历", description = "用于处理个人履历相关操作")
@RestController
@RequestMapping("/api/doc")
public class DocController {
    private final DocService docService;

    @Autowired
    public DocController(DocService docService) {
        this.docService = docService;
    }

    @Operation(summary = "新增个人履历")
    @PostMapping
    public Result createDoc(@RequestBody DocDTO docDTO) {
        return docService.addNewDoc(docDTO);
    }

    @Operation(summary = "编辑个人履历")
    @PutMapping("/{id}")
    public Result updateDoc(@PathVariable Integer id, @RequestBody DocDTO docDTO) {
        return docService.editDoc(id, docDTO);
    }

    @Operation(summary = "删除个人履历")
    @DeleteMapping("/{id}")
    public Result deleteDoc(@PathVariable Integer id) {
        return docService.deleteDoc(id);
    }

    @Operation(summary = "获取个人履历列表")
    @GetMapping
    public Result listDocs() {
        return docService.getAllDocs();
    }

}
