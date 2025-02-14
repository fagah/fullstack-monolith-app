export interface PatientDetail {
  id: string;
  personalInfo: {
    fullName: string;
    dateOfBirth: Date;
    gender: string;
    bloodGroup: string;
    contactNumber: string;
    email: string;
    address: string;
    emergencyContact: {
      name: string;
      relationship: string;
      phone: string;
    };
  };
  medicalSummary: {
    allergies: string[];
    chronicConditions: string[];
    currentMedications: string[];
    pastSurgeries: Array<{
      procedure: string;
      date: Date;
      hospital: string;
      surgeon: string;
    }>;
  };
  appointmentHistory: Array<{
    id: string;
    date: Date;
    doctorName: string;
    specialty: string;
    type: string;
    status: string;
    diagnosis?: string;
    prescription?: Array<{
      medication: string;
      dosage: string;
      frequency: string;
      duration: string;
    }>;
    notes?: string;
  }>;
  treatmentHistory: Array<{
    startDate: Date;
    endDate?: Date;
    condition: string;
    treatingDoctor: string;
    medications: Array<{
      name: string;
      dosage: string;
      frequency: string;
    }>;
    outcome?: string;
  }>;
  medicalTests: Array<{
    id: string;
    date: Date;
    testName: string;
    category: string;
    requestedBy: string;
    results: string;
    normalRange?: string;
    interpretation?: string;
    documentUrl?: string;
  }>;
  vitals: Array<{
    date: Date;
    bloodPressure: string;
    heartRate: number;
    temperature: number;
    weight: number;
    height: number;
    bmi: number;
    notes?: string;
  }>;
  statistics: {
    totalAppointments: number;
    cancelledAppointments: number;
    completedAppointments: number;
    uniqueDoctors: number;
    lastVisit: Date;
    nextAppointment?: Date;
    averageVisitsPerYear: number;
  };
  documents: Array<{
    id: string;
    type: string;
    name: string;
    date: Date;
    category: string;
    uploadedBy: string;
    url: string;
  }>;
  insuranceInfo?: {
    provider: string;
    policyNumber: string;
    validUntil: Date;
    coverageDetails: string;
  };
}