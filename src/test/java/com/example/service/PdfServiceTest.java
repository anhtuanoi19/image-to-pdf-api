import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.core.io.ByteArrayResource;

class PdfServiceTest {

    @InjectMocks
    private PdfService pdfService;

    @Mock
    private ByteArrayResource mockImageResource;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGeneratePdf() {
        // Given a mock image resource
        when(mockImageResource.getByteArray()).thenReturn(new byte[]{1, 2, 3});

        // When generating PDF
        ByteArrayResource pdfResource = pdfService.generatePdf(mockImageResource);

        // Then the PDF resource should not be null
        assertNotNull(pdfResource);
    }
}