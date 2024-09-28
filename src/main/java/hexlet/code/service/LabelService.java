package hexlet.code.service;

import hexlet.code.dto.label.LabelCreateDTO;
import hexlet.code.dto.label.LabelDTO;
import hexlet.code.dto.label.LabelUpdateDTO;
import hexlet.code.exception.ResourceNotFoundException;
import hexlet.code.mapper.LabelMapper;
import hexlet.code.repository.LabelRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LabelService {

    @Autowired
    private LabelRepository labelRepository;

    @Autowired
    private LabelMapper labelMapper;

    public List<LabelDTO> findAll() {
        var labels = labelRepository.findAll();

        return labels.stream()
                .map(labelMapper::map)
                .toList();
    }

    public LabelDTO findById(Long id) {
        var label = labelRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Label with id " + id + " not found"));
        var labelDTO = labelMapper.map(label);

        return labelDTO;
    }

    public LabelDTO create(LabelCreateDTO labelCreateData) {
        var label = labelMapper.create(labelCreateData);
        labelRepository.save(label);
        var labelDTO = labelMapper.map(label);

        return labelDTO;
    }

    public LabelDTO update(LabelUpdateDTO labelUpdateData, Long id) {
        var label = labelRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Label with id " + id + " not found"));
        labelMapper.update(labelUpdateData, label);
        labelRepository.save(label);

        var labelDTO = labelMapper.map(label);

        return labelDTO;
    }

    public void delete(Long id) {
        labelRepository.deleteById(id);
    }
}
