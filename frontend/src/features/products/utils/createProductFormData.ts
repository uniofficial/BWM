export function createProductImageFormData(images: File[]) {
  const formData = new FormData()
  images.forEach((image) => formData.append('images', image))
  formData.append('representativeIndex', '0')
  return formData
}
