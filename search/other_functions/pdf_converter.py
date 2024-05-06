import PyPDF2

# create file object variable
# opening method will be rb
pdffileobj = open(r"C:\Users\admin\Downloads\Modelirovanie_sistem_LP_3_glava.pdf", 'rb')
text = ""

# create reader variable that will read the pdffileobj
pdfreader = PyPDF2.PdfReader(pdffileobj)

for i in range(0, len(pdfreader.pages)):
    page_obj = pdfreader.pages[i]
    text += page_obj.extract_text() + '\n'

print(text)

